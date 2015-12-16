package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static reactor.bus.selector.Selectors.$;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.reactivestreams.Processor;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.io.buffer.Buffer;

public class TcpServerImpl implements TcpServer, Runnable {
	protected EventBus bus;
	protected final ConcurrentHashMap<SocketChannel, TcpChannelImpl> channelMap = new ConcurrentHashMap<>();
	protected final Selector selector;
	/**
	 * Server socket
	 */
	protected final ServerSocketChannel server;
	/**
	 * Address to connect to
	 */
	protected final SocketAddress addr;
	/**
	 * E
	 */
	protected ExecutorService executor;
	protected final AtomicBoolean running = new AtomicBoolean(false);
	
	
	public TcpServerImpl(int port) throws IOException {
		this(new InetSocketAddress(port));
	}
	public TcpServerImpl(SocketAddress addr) throws IOException {
		this.addr = addr;
		System.out.println("Binding to " + addr.toString());
		selector = Selector.open();
		server = ServerSocketChannel.open();
		server.socket().bind(addr);
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
	}
	@Override
	public TcpServerImpl start(Consumer<? super TcpServer> initializer) throws IOException, IllegalStateException {
		initializer.accept(this);
		return this.start();
	}
	public TcpServerImpl start() {
		if (executor == null)
			run();
		else
			executor.submit(this);
		return this;
	}
	public TcpServerImpl dispatchOn(Processor<Event<?>, Event<?>> p) {
		this.bus = EventBus.create(p);
		return this;
	}
	public TcpServerImpl runOn(ExecutorService executor) {
		if (!running.get())
			this.executor = executor;
		return this;
	}
	
	@Override
	public SocketAddress getAddress() {
		return addr;
	}

	@Override
	public boolean isRunning() {
		return running.get();
	}

	@Override
	public boolean isSSL() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TcpServer onConnect(BiConsumer<TcpServer, TcpChannel> handler) {
		this.bus.on($("connect"), event -> handler.accept(this, ((Event<TcpChannel>)event).getData()));
		return this;
	}

	@Override
	public boolean stop() throws IOException, InterruptedException {
		this.server.close();
		this.selector.close();
		System.out.println("Bye!");
		this.executor.shutdown();
		return true;
	}

	@Override
	public boolean stop(Duration timeout) throws IOException, InterruptedException {
		return stop();
	}
	public void run() {
		if (!running.weakCompareAndSet(false, true))
			throw new IllegalStateException("Was already running!");
		try {
			while (true) {
				try {
					int nKeys = selector.select();
					if (nKeys > 0) {
						Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							keyIterator.remove();
							if (!key.isValid())
								continue;
							
							if (key.isAcceptable()) {
								this.accept(key);
							} else {
								if (key.isReadable())
									this.read(key);
								if (key.isWritable())
									this.write(key);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} finally {
			if (!running.weakCompareAndSet(true, false))
				throw new IllegalStateException("...I'm not even sure what caused this...");
		}
	}
	/**
	 * Connect to a socket
	 * @param key
	 * @throws IOException
	 */
	protected void accept(SelectionKey key) throws IOException {
		SocketChannel socket = server.accept();
		socket.configureBlocking(false);
		socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		socket.setOption(StandardSocketOptions.TCP_NODELAY, true);
		socket.register(selector, SelectionKey.OP_READ, null);
		TcpChannelImpl channel = new TcpChannelImpl(this, socket);
		channelMap.put(socket, channel);
		bus.notify("connect",Event.<TcpChannel>wrap(channel));
		System.out.println("Connected to "+channel.getRemoteAddress().toString());
	}
	protected void read(SelectionKey key) throws IOException {
		System.out.println("Reading...");
		SocketChannel socket = (SocketChannel) key.channel();
		Buffer buffer = readSocket(socket);
		TcpChannelImpl channel = channelMap.get(socket);
		bus.notify("read.chan"+channel.getConnectionID(), Event.wrap(key));
	}
	protected Buffer readSocket(SocketChannel socket) throws IOException {
		return null; //TODO finish
	}
	protected void write(SelectionKey key) {
		bus.notify("write",Event.wrap(key));
	}
}

package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static reactor.bus.selector.Selectors.$;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.reactivestreams.Processor;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class TcpServerImpl implements TcpServer, Runnable {
	protected EventBus bus;
	protected final Selector selector;
	/**
	 * Server socket
	 */
	protected final ServerSocketChannel server;
	/**
	 * Address to connect to
	 */
	protected final InetSocketAddress addr;
	/**
	 * E
	 */
	protected ExecutorService executor;
	protected final AtomicBoolean running = new AtomicBoolean(false);
	public TcpServerImpl(InetSocketAddress addr) throws IOException {
		this.addr = addr;
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
	public TcpServerImpl dispatchOn(Processor<Event<?>, Event<?>> p) {
		this.bus = EventBus.create(p);
		return this;
	}
	public TcpServerImpl runOn(ExecutorService executor) {
		if (!running.get())
			this.executor = executor;
		return this;
	}
	public TcpServerImpl start() {
		if (executor == null)
			run();
		else
			executor.submit(this);
		return this;
	}
	
	@Override
	public InetSocketAddress getAddress() {
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(Duration timeout) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
	public void run() {
		if (!running.weakCompareAndSet(false, true))
			throw new IllegalStateException("Was already running!");
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
	}
	protected void accept(SelectionKey key) throws IOException {
		SocketChannel channel = server.accept();
		channel.configureBlocking(false);
		channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		channel.register(selector, SelectionKey.OP_READ, null);
		bus.notify("connect",Event.wrap(null));
		System.out.println("Connected to "+channel.getRemoteAddress().toString());
	}
	protected void read(SelectionKey key) {
		System.out.println("Reading...");
		bus.notify("read",Event.wrap(key));
	}
	protected void write(SelectionKey key) {
		bus.notify("write",Event.wrap(key));
	}
}

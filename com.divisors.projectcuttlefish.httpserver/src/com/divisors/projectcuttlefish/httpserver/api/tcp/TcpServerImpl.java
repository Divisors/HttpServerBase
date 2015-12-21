package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static reactor.bus.selector.Selectors.$;
import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.reactivestreams.Processor;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

/**
 * Implementation of {@link TcpServer}. 
 * @author mailmindlin
 * @see TcpServer
 */
public class TcpServerImpl implements TcpServer, Runnable {
	public static final int BUFFER_SIZE = 4096;
	protected EventBus bus;
	protected final ConcurrentHashMap<Long, TcpChannelImpl> channelMap = new ConcurrentHashMap<>();
	protected final AtomicLong nextId = new AtomicLong(0L);
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
	protected ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	
	
	public TcpServerImpl(int port) throws IOException {
		this(new InetSocketAddress("localhost",port));
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
	public TcpServer onConnect(Consumer<TcpChannel> handler) {
		this.bus.on($("tcp.connect"), event -> handler.accept(((Event<TcpChannel>)event).getData()));
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
		System.out.println("Accepting...");
		SocketChannel socket = server.accept();
		socket.configureBlocking(false);
		socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		socket.setOption(StandardSocketOptions.TCP_NODELAY, true);//We should *hopefully* be writing large amounts of data at a time, so this just decreases the RTT
		
		long id = this.nextId.incrementAndGet();
		TcpChannelImpl channel = new TcpChannelImpl(this, socket, id);
		channelMap.put(id, channel);
		socket.register(selector, SelectionKey.OP_READ, id);
		
		bus.notify("tcp.connect",Event.<TcpChannel>wrap(channel));
		System.out.println("Connected to "+channel.getRemoteAddress().toString());
	}
	protected void read(SelectionKey key) throws IOException {
		System.out.println("188 | Reading...");
		long id = (Long)key.attachment();
		TcpChannelImpl channel = this.channelMap.get(id);
		SocketChannel socket = (SocketChannel)key.channel();
		
		this.readBuffer.clear();
		//read from socket
		int read = socket.read(this.readBuffer);
		
		if (read < 0) {
			//close channel
			System.out.println("199 | Closing channel...");
			channel.close();
			this.channelMap.remove(channel.getConnectionID());
			return;
		}
		
		this.readBuffer.flip();
		//make a slower buffer that's the size of the bytes read
		ByteBuffer buffer = ByteBuffer.allocate(read);
		buffer.put(this.readBuffer);
		buffer.flip();
		
		//send event
		bus.notify(Tuple.<String,Long>of("tcp.read", channel.getConnectionID()), Event.<ByteBuffer>wrap(buffer));
		
		//FOR TESTING
//		byte[] arr = buffer.array();
//		System.out.println("Read " + read + " bytes from " + socket.getRemoteAddress());
//		System.out.println(FormatUtils.bytesToHex(arr));
//		System.out.println(new String(arr));
		
		//register socket with selector
		try {
			socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			this.channelMap.remove(id);
			e.printStackTrace();
			return;
		}
	}
	protected void write(SelectionKey key) {
		System.out.println("Writing...");
		long id = (Long)key.attachment();
		try {
			((SocketChannel)key.channel()).register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		} catch (ClosedChannelException e) {
			this.channelMap.remove(id);
			e.printStackTrace();
			return;
		}
		//TODO finish (or make it do... anything whatsoever)
		bus.notify("tcp.write",Event.wrap(this.channelMap.get(id)));
	}
}

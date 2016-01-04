package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static reactor.bus.selector.Selectors.$;
//import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

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
import java.util.function.Predicate;

import org.reactivestreams.Processor;

import com.divisors.projectcuttlefish.httpserver.api.Server;

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
	public boolean isSecure() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TcpServer onConnect(Consumer<TcpChannel> handler) {
		this.bus.on($("tcp.connect"), event -> handler.accept(((Event<TcpChannel>)event).getData()));
		return this;
	}

	@Override
	public boolean shutdownNow() throws IOException, InterruptedException {
		this.running.set(false);
		this.server.close();
		this.selector.close();
		System.out.println("Bye!");
		this.executor.shutdownNow();
		return true;
	}
	public void run() {
		if (!running.weakCompareAndSet(false, true))
			throw new IllegalStateException("Was already running!");
		try {
			while (true) {
				try {
					System.out.println("Polling...");
					int nKeys = selector.select();
					if (nKeys > 0) {
						System.out.println(""+nKeys+ " keys");
						Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							keyIterator.remove();
							System.out.println(key.toString() + " | " + key.interestOps() + " |V" + key.isValid() + " |A" + key.isAcceptable() + " |R" + key.isReadable() + " |W" + key.isWritable() + " |C" + key.isConnectable());
							if (!key.isValid())
								continue;
							
							if (key.isAcceptable()) {
								this.accept(key);
							} else {
								if (key.isValid() && key.isReadable())
									this.read(key);
								if (key.isValid() && key.isWritable())
									this.write(key);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!running.weakCompareAndSet(true, false))
				throw new IllegalStateException("...I'm not even sure what caused this...");
		}
	}
	/**
	 * Accept an incoming connection.
	 * @param key selection key
	 * @throws IOException if there was a problem setting it up
	 */
	protected void accept(SelectionKey key) throws IOException {
		System.out.println("Accepting...");
		SocketChannel socket = server.accept();
		socket.configureBlocking(false);
		socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		socket.setOption(StandardSocketOptions.TCP_NODELAY, true);//We should *hopefully* be writing large amounts of data at a time, so this just decreases the RTT
		
		long id = this.nextId.incrementAndGet();
		TcpChannelImpl channel = upgradeSocket(socket, id);
		channelMap.put(id, channel);
		bus.notify("tcp.connect",Event.<TcpChannel>wrap(channel));
		System.out.println("\tAccepted from "+channel.getRemoteAddress().toString());
		System.out.println("\tID #"+id);
		
		socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, id);
		System.out.println("\tDone Accepting");
	}
	protected TcpChannelImpl upgradeSocket(SocketChannel socket, long id) {
		return new TcpChannelImpl(this, socket, id);
	}
	/**
	 * Read buffer of data from socket
	 * @param key
	 * @throws IOException
	 */
	protected void read(SelectionKey key) throws IOException {
		long id = (Long)key.attachment();
		System.out.println("Reading #" + id + "...");
		TcpChannelImpl channel = this.channelMap.get(id);
		SocketChannel socket = (SocketChannel)key.channel();
		
		this.readBuffer.clear();
		//read from socket
		int read = socket.read(this.readBuffer);
		
		if (read < 0) {
			//close channel
			System.out.println("\tClosing channel...");
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
			socket.register(selector, SelectionKey.OP_READ, id);
		} catch (ClosedChannelException e) {
			this.channelMap.remove(id);
			channel.close();
			e.printStackTrace();
			return;
		}
	}
	protected void write(SelectionKey key) throws IOException {
		Object attachment = key.attachment();
		if (!key.isValid()) {
			System.err.println("Invalid key"+attachment);
			return;
		}
		long id = (Long)attachment;
		System.out.println("Writing #" + id + "...");
		
		TcpChannelImpl channel = this.channelMap.get(id);
		SocketChannel socket = (SocketChannel)key.channel();

		channel.doWrite();
		
		try {
			socket.register(selector, SelectionKey.OP_READ, id);
		} catch (ClosedChannelException e) {
			this.channelMap.remove(id);
			channel.close();
			e.printStackTrace();
			return;
		}
	}
	@Override
	public Server<ByteBuffer, ByteBuffer, TcpChannel> onConnect(Predicate<TcpChannel> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean shutdown() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean shutdown(Duration timeout) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isShuttingDown() {
		// TODO Auto-generated method stub
		return false;
	}
}

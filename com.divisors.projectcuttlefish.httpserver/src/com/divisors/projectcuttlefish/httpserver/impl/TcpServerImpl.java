package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;

import reactor.rx.Stream;

public class TcpServerImpl implements TcpServer {
	
	public static final int BUFFER_SIZE = 8192;
	/**
	 * Address that the server is bound to
	 */
	protected final InetSocketAddress address;
	/**
	 * Channel that all the requests come through
	 */
	protected ServerSocketChannel serverSocketChannel;
	/**
	 * Selector to find stuff
	 */
	protected Selector selector;
	/**
	 * Map of connections currently open
	 */
	protected ConcurrentHashMap<Long, TcpConnection> dataQueue = new ConcurrentHashMap<>();
	/**
	 * 
	 */
	protected volatile WeakReference<Thread> self = new WeakReference<>(null);
	protected ByteBuffer readBuffer, writeBuffer;
	protected AtomicLong nextID = new AtomicLong(16 * 1024);
	private Stream<TcpConnection> connectionStream;
	
	public TcpServerImpl(int port) {
		this(new InetSocketAddress(port));
	}
	
	public TcpServerImpl(InetSocketAddress addr) {
		address = addr;
	}
	
	public InetSocketAddress getAddress() {
		return address;
	}
	
	@Override
	public void init() throws IOException {
		System.out.println("Initializing");
		// TODO maybe allocateDirect?
		readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		writeBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		this.selector = Selector.open();
		
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.socket().bind(address);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				
		/*this.connectionStream = Streams.<Connection, Void>createWith(
				(demand, sub) -> {
					sub.context();
					System.out.println("Demanding "+demand);
				},
				sub->null,
				sub->System.out.println("Cancelled"));*/
		System.out.println("Initialized");
	}
	
	@Override
	public void destroy() throws IOException {
		readBuffer = null;// dereference bytebuffer
		serverSocketChannel.socket().close();
		serverSocketChannel.close();
		for (TcpConnection connection : dataQueue.values())
			connection.close();
		System.out.println("Destroyed");
	}
	
	@Override
	public void start() throws IOException {
		run();
	}
	
	@Override
	public void start(ExecutorService executor) throws IOException {
		executor.submit(this);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean stop() {
		synchronized (self) {
			if (self.get() == null)
				return false;
			self.get().interrupt();
			self.get().stop();
			return true;
		}
	}
	
	@Override
	public boolean stop(Duration timeout) {
		return stop();
	}
	
	@Override
	public void run() {
		System.out.println("Running on " + this.getAddress().toString());
		self = new WeakReference<>(Thread.currentThread());
		if (selector == null || serverSocketChannel == null || !serverSocketChannel.isOpen())
			throw new IllegalStateException("init() must be called before run()");
		while (!Thread.interrupted()) {
			try {
				selector.select();
				Iterator<SelectionKey> keyIterator = this.selector.selectedKeys().iterator();
				while (keyIterator.hasNext() && !Thread.currentThread().isInterrupted()) {
					SelectionKey key = keyIterator.next();
					
					keyIterator.remove();
					
					if (!key.isValid())
						continue;
						
					if (key.isAcceptable())
						accept(key);
					else if (key.isReadable())
						read(key);
					else if (key.isWritable())
						write(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("Interrupted");
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	protected void accept(SelectionKey key) throws UnsupportedEncodingException, IOException {
		System.out.println("Connecting to client");
		ServerSocketChannel sChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = sChannel.accept();
		channel.configureBlocking(false);
		
		// write welcome message
//		channel.write(ByteBuffer.wrap("Welcome, this is the echo server\r\n".getBytes("US-ASCII")));
		
		SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
		System.out.println("Connected to: " + remoteAddr);
		
		this.registerChannel(key, channel);
	}
	
	/**
	 * Register channel with 
	 */
	protected void registerChannel(SelectionKey key, SocketChannel channel) throws ClosedChannelException {
		// register channel with selector for further IO
		long id = this.nextID.incrementAndGet();
		TcpConnection connection = new TcpConnectionImpl(channel);
		connection.setConnectionID(id);
		this.dataQueue.put(id, connection);
		SelectionKey readKey = channel.register(this.selector, SelectionKey.OP_READ);
		readKey.attach(id);
		
		connection.add(("HTTP/1.1 200 OK\r\n"
				+ "Content-Type: text/html; charset=iso-8859-1\r\n"
				+ "Server: x-projectcuttlefish\r\n"
				+ "\r\n"
				+ "Hello, World!\r\n").getBytes());
	}
	
	protected void write(SelectionKey key) throws IOException {
		TcpConnection connection = this.dataQueue.get((Long) key.attachment());
		
		if (!connection.isOpen()) {
			key.cancel();
			this.dataQueue.remove(connection.getConnectionID());
			SocketAddress remoteAddr = ((SocketChannel) key.channel()).socket().getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			connection.close();
		}
		connection.writeNext(this.writeBuffer);
		
		connection.close();
		
//		key.interestOps(key.interestOps() | SelectionKey.OP_READ);
	}
	
	protected void read(SelectionKey key) throws IOException, InterruptedException {
		TcpConnection connection = this.dataQueue.get((Long) key.attachment());
		
		long read;
		try {
			read = connection.readNext(this.readBuffer);
		} catch (IOException e) {
			key.cancel();
			this.dataQueue.remove(connection.getConnectionID());
			e.printStackTrace();
			connection.close();
			return;
		}
		if (read == -1) {
			this.dataQueue.remove(connection.getConnectionID());
			SocketAddress remoteAddr = ((SocketChannel) key.channel()).socket().getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			key.cancel();
			connection.close();
			return;
		}
		
		byte[] data = connection.peek();
		if (data != null)
			System.out.println("Got: " + Arrays.toString((new String(data, "US-ASCII").split("\n"))));
		// write back to client
		key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
	}
	
	@Override
	public boolean isRunning() {
		return this.self.get() != null;
	}
	
	@Override
	public void registerConnectionListener(BiConsumer<TcpConnection, TcpServer> handler) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void deregisterConnectionListener(BiConsumer<TcpConnection, TcpServer> handler) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public boolean isSSL() {
		return false;
	}
	
	@Override
	public boolean shutdown() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean shutdown(Duration timeout) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean shutdownNow() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	public Stream<TcpConnection> connectionStream() {
		return this.connectionStream;
	}
}

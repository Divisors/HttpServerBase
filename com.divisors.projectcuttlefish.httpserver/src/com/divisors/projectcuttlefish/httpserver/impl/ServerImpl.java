package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.api.Server;

public class ServerImpl implements Server {
	public static final int BUFFER_SIZE = 8192;
	protected final InetSocketAddress address;
	protected ServerSocketChannel serverSocketChannel;
	protected final ArrayList<BiConsumer<Socket, ? super Server>> handlers = new ArrayList<>();
	protected Selector selector;
	protected ConcurrentHashMap<SocketChannel, List<byte[]>> dataMap = new ConcurrentHashMap<>();
	protected WeakReference<Thread> self;
	public ServerImpl(int port) {
		this(new InetSocketAddress(port));
	}

	public ServerImpl(InetSocketAddress addr) {
		address = addr;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public void init() throws IOException {
		this.selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);

		serverSocketChannel.socket().bind(address);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public void destroy() throws IOException {
		serverSocketChannel.socket().close();
		serverSocketChannel.close();
	}

	@Override
	public void start() throws IOException {
		if (serverSocketChannel == null)
			init();
		run();
	}

	@Override
	public void start(ExecutorService executor) throws IOException {
		if (serverSocketChannel == null)
			init();
		executor.submit(this);
	}

	@Override
	public void stop() {
		self.get().interrupt();
		self.get().stop();
	}

	@Override
	public void stop(Duration timeout) {
		
	}

	@Override
	public void run() {
		self = new WeakReference<>(Thread.currentThread());
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
			}
		}
	}

	protected void write(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();
		List<byte[]> pendingData = this.dataMap.get(channel);
		Iterator<byte[]> items = pendingData.iterator();
		while (items.hasNext()) {
			byte[] item = items.next();
			items.remove();
			channel.write(ByteBuffer.wrap(item));
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	protected void read(SelectionKey key) throws IOException {
		SocketChannel channel = (SocketChannel) key.channel();

		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int numRead = -1;
		try {
			numRead = channel.read(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (numRead == -1) {
			this.dataMap.remove(channel);
			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			System.out.println("Connection closed by client: " + remoteAddr);
			channel.close();
			key.cancel();
			return;
		}

		byte[] data = new byte[numRead];
		System.arraycopy(buffer.array(), 0, data, 0, numRead);
		System.out.println("Got: " + new String(data, "US-ASCII"));
		// write back to client
		doEcho(key, data);
	}

	protected void accept(SelectionKey key) throws UnsupportedEncodingException, IOException {
		ServerSocketChannel sChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel = sChannel.accept();
		channel.configureBlocking(false);

		// write welcome message
		channel.write(ByteBuffer.wrap("Welcome, this is the echo server\r\n".getBytes("US-ASCII")));

		Socket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		System.out.println("Connected to: " + remoteAddr);

		// register channel with selector for further IO
		dataMap.put(channel, new ArrayList<byte[]>());
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerConnectionListener(BiConsumer<Socket, Server> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deregisterConnectionListener(BiConsumer<Socket, Server> handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSSL() {
		return false;
	}

	private void doEcho(SelectionKey key, byte[] data) {
		SocketChannel channel = (SocketChannel) key.channel();
		List<byte[]> pendingData = this.dataMap.get(channel);
		pendingData.add(data);
		key.interestOps(SelectionKey.OP_WRITE);
	}
}

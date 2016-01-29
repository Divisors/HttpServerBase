package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static reactor.bus.selector.Selectors.$;
//import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

/**
 * Implementation of {@link TcpServer}. 
 * @author mailmindlin
 * @see TcpServer
 */
public class TcpServerImpl implements TcpServer {
	public static final int BUFFER_SIZE = 4096;
	protected EventBus bus;
	protected final ConcurrentHashMap<Long, TcpChannelImpl> channelMap = new ConcurrentHashMap<>();
	protected final AtomicLong nextId = new AtomicLong(0L);
	protected Selector selector;
	/**
	 * Server socket
	 */
	protected ServerSocketChannel server;
	/**
	 * Address to connect to
	 */
	protected final SocketAddress addr;
	/**
	 * Executor that this server runs on
	 */
	protected ExecutorService executor;
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	protected ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	
	
	public TcpServerImpl(int port) {
		this(new InetSocketAddress(port));
	}
	public TcpServerImpl(SocketAddress addr) {
		this(null, null, addr);
	}
	public TcpServerImpl(ExecutorService executor, SocketAddress addr) {
		this(null, executor, addr);
	}
	public TcpServerImpl(EventBus bus, SocketAddress addr) {
		this(bus, null, addr);
	}
	public TcpServerImpl(EventBus bus, ExecutorService executor, SocketAddress addr) {
		this.bus = bus;
		this.executor = executor;
		this.addr = addr;
		System.out.println("TCP::Binding to " + addr.toString());
	}
	@Override
	public TcpServerImpl init() throws IllegalStateException, IOException {
		if (getState() != ServiceState.UNINITIALIZED)
			throw new IllegalStateException("Expected: UNITNITIALIZED; State: "+getState().name());
		selector = Selector.open();
		server = ServerSocketChannel.open();
		server.socket().bind(addr);
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
		state.set(ServiceState.INITIALIZED);
		return this;
	}
	@Override
	public TcpServerImpl start() {
		if (!state.compareAndSet(ServiceState.INITIALIZED, ServiceState.STARTING))
			throw new IllegalStateException("Expected: INITIALIZED; State: "+state.get().name());
		if (executor == null) {
			run();
		} else {
			executor.submit(this);
		}
		return this;
	}
	@Override
	public TcpServerImpl start(Consumer<? super TcpServer> initializer) throws IOException, IllegalStateException {
		if (!state.compareAndSet(ServiceState.INITIALIZED, ServiceState.STARTING))
			throw new IllegalStateException("Expected: INITIALIZED; State: "+state.get().name());
		initializer.accept(this);
		if (executor == null) {
			run();
		} else {
			executor.submit(this);
		}
		return this;
	}
	@Override
	public TcpServerImpl dispatchOn(EventBus bus) {
		this.bus = bus;
		return this;
	}
	@Override
	public TcpServerImpl runOn(ExecutorService executor) {
		final ServiceState cstate = getState();
		if (cstate == ServiceState.UNINITIALIZED || cstate == ServiceState.INITIALIZED || cstate == ServiceState.STARTING)
			this.executor = executor;
		else
			throw new IllegalStateException("Expected: INITIALIZED or STARTING; State: "+cstate.name());	
		return this;
	}
	@Override
	public SocketAddress getAddress() {
		return addr;
	}

	@Override
	public boolean isSecure() {
		return false;//not TLS
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Action onConnect(Consumer<TcpChannel> handler) {
		return new RegistrationCancelAction(this.bus.on($("tcp.connect"), event -> handler.accept(((Event<TcpChannel>)event).getData())));
	}
	
	@Override
	public void run() {
		if (!state.compareAndSet(ServiceState.STARTING, ServiceState.RUNNING))
			throw new IllegalStateException("I was already running! (Expect: STARTING; was " + state.get() + ").");
		try {
			ServiceState cstate;//temporary state mirror for making life easy

			while ((cstate = getState()) == ServiceState.RUNNING || (cstate == ServiceState.STOPPING && channelMap.size() > 0)) {
				try {
					int nKeys = 0;//number of keys selected
					System.out.println("TCP::Polling... (" + this.channelMap.size() + " channels open)");
					if ((cstate = getState()) == ServiceState.STOPPING)
						nKeys = selector.select(1000);//the server is stopping now, so ensure that this doesn't hang for as long.
					else
						nKeys = selector.select();//there are some open channels & the server isn't closing, so we don't have to check as much
					
					if (nKeys > 0) {
						System.out.println("\tGot " + nKeys + " key(s)");
						Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							keyIterator.remove();
							System.out.println("\tKey : " + key.interestOps() + " |" + (key.isValid()?" Valid":"") + (key.isAcceptable()?" Acceptable":"") + (key.isReadable()?" Readable":"") + (key.isWritable()?" Writable":"") + (key.isConnectable()?" Connectable":""));
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
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!(this.state.compareAndSet(ServiceState.RUNNING, ServiceState.INITIALIZED) || this.state.compareAndSet(ServiceState.STOPPING, ServiceState.INITIALIZED)))
				throw new IllegalStateException("...I'm not even sure what caused this...");//TODO: set state to destroyed
			this.state.notifyAll();
		}
	}
	/**
	 * Accept an incoming connection.
	 * @param key selection key
	 * @throws IOException if there was a problem setting it up
	 */
	protected void accept(SelectionKey key) throws IOException {
		System.out.println("TCP::Accepting...");
		SocketChannel socket = server.accept();
		
		//if shutting down, don't accept any new threads
		if (getState() == ServiceState.STOPPING) {
			socket.close();
			key.cancel();
			System.out.println("\tRejected thread (Shutting down).");
			return;
		}
		
		//setup socket
		socket.configureBlocking(false);
		socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		socket.setOption(StandardSocketOptions.TCP_NODELAY, true);//We should *hopefully* be writing large amounts of data at a time, so this just decreases the RTT
		
		//create channel
		long id = this.nextId.incrementAndGet();
		TcpChannelImpl channel = upgradeSocket(socket, id);
		channelMap.put(id, channel);
		System.out.println("\tAccepted from "+channel.getRemoteAddress().toString());
		System.out.println("\tID #"+id);
		
		//trigger handlers
		Event.Headers eventHeaders = new Event.Headers();
		eventHeaders.setOrigin("TcpServerImpl@"+addr.toString());
		bus.notify("tcp.connect",new Event<TcpChannel>(eventHeaders, channel));
		
		//queue for future I/O
		socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, id);
		System.out.println("\tDone.");
	}
	protected TcpChannelImpl upgradeSocket(SocketChannel socket, long id) {
		//this method can be overridden by children of this class offering encryption & stuff
		return new TcpChannelImpl(this, socket, id);
	}
	/**
	 * Read buffer of data from socket
	 * @param key
	 * @throws IOException
	 */
	protected void read(SelectionKey key) throws IOException {
		//get socket & channel
		long id = (Long)key.attachment();
		System.out.println("TCP::Reading #" + id + "...");
		TcpChannelImpl channel = this.channelMap.get(id);
		SocketChannel socket = (SocketChannel)key.channel();
		
		//read from socket
		this.readBuffer.clear();
		int read;//number of bytes read
		try {
			read = socket.read(this.readBuffer);
			//if read<0, then the channel is closed
			if (read < 0) {
				//close channel
				System.out.println("\tClosing channel...");
				channel.close();
				return;
			}
		} catch (IOException e) {
			channel.close();
			throw e;
		}
		
		this.readBuffer.flip();
		//make a slower buffer that's the size of the bytes read
		ByteBuffer buffer = ByteBuffer.allocate(read);
		buffer.put(this.readBuffer);
		buffer.flip();
		
		//send event
		System.out.println("TCP::Read " + read + " bytes from #" + id);
		bus.notify(Tuple.<String,Long>of("tcp.read", channel.getConnectionID()), Event.<ByteBuffer>wrap(buffer));
		
		//FOR TESTING
//		byte[] arr = buffer.array();
//		System.out.println("Read " + read + " bytes from " + socket.getRemoteAddress());
//		System.out.println(FormatUtils.bytesToHex(arr));
//		System.out.println(new String(arr));
		
		//register socket with selector
		if (!((SocketChannel)key.channel()).isOpen()) {
			try {
				channel.close();
			} catch (IOException e) {
				//the socket was already closed
			}
			channelMap.remove(id);
		} else {
			key.interestOps(SelectionKey.OP_READ);
		}
	}
	protected void write(SelectionKey key) throws IOException {
		Object attachment = key.attachment();
		if (!key.isValid()) {
			System.err.println("TCP::Invalid key" + attachment);
			return;
		}
		long id = (Long)attachment;
		System.out.println("TCP::Writing #" + id + "...");
		
		TcpChannelImpl channel = this.channelMap.get(id);

		channel.doWrite();
		
		if (!((SocketChannel)key.channel()).isOpen()) {
			try {
				channel.close();
			} catch (IOException e) {
				//the socket was already closed
			}
			channelMap.remove(id);
		} else {
			key.interestOps(SelectionKey.OP_READ);
		}
	}
	@Override
	public ServiceState getState() {
		return this.state.get();
	}
	@Override
	public boolean shutdown() {
		if (this.state.compareAndSet(ServiceState.RUNNING, ServiceState.STOPPING) || getState() == ServiceState.STOPPING) {
			this.selector.wakeup();
			return true;
		}
		return false;
	}
	@Override
	public boolean shutdown(Duration timeout) throws InterruptedException {
		if(!this.state.compareAndSet(ServiceState.RUNNING, ServiceState.STOPPING) && getState() != ServiceState.STOPPING)
			return true;
		this.selector.wakeup();
		Instant now = null, end = Instant.now().plus(timeout);
		while (getState() == ServiceState.STOPPING && end.isAfter(now = Instant.now())) {
			Duration remaining = Duration.between(now, end);
			this.state.wait(remaining.toMillis(), remaining.getNano() % 1000);
		}
		ServiceState state = getState();
		return state == ServiceState.DESTROYED || state == ServiceState.INITIALIZED;
	}
	@Override
	public boolean shutdownNow() throws IOException, InterruptedException {
		if ((!this.state.compareAndSet(ServiceState.RUNNING, ServiceState.STOPPING)) && getState() != ServiceState.STOPPING)
			return false;
		this.selector.wakeup();
		this.executor.shutdownNow();
		final boolean result = this.executor.awaitTermination(20, TimeUnit.MILLISECONDS);
		this.state.compareAndSet(ServiceState.STOPPING, ServiceState.INITIALIZED);
		System.out.println("Bye!");
		return result;
	}
	@Override
	public void destroy() throws RuntimeException {
		try {
			this.bus = null;
			this.server.close();
			this.server = null;
			this.selector.close();
			this.selector = null;
			this.channelMap.clear();
			this.executor.shutdownNow();//is this right?
			this.executor = null;
		} catch (IOException e) {
			this.state.set(ServiceState.DESTROYED);
			throw new RuntimeException(e);
		}
		this.state.set(ServiceState.UNINITIALIZED);
	}
}

package com.divisors.projectcuttlefish.httpserver.client;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.error.ServiceStateException;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

/**
 * Non-blocking TCP client implementation. 
 * @author mailmindlin
 */
public class TcpClient implements Server<ByteBuffer, ByteBuffer, TcpClientChannel> {
	public static final int BUFFER_SIZE = 4096;
	protected EventBus bus;
	protected final ConcurrentHashMap<Long, TcpClientChannel> channelMap = new ConcurrentHashMap<>();
	protected final AtomicLong nextId = new AtomicLong(0L);
	protected Selector selector;
	/**
	 * Executor that this server runs on
	 */
	protected ExecutorService executor;
	/**
	 * Current state of this
	 */
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	protected ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	protected final Deque<Consumer<Selector>> selectorUpdateQueue = new LinkedList<>();//TODO better queue
	
	public TcpClient() throws IOException {
	}
	public TcpClient(EventBus bus) {
		this(bus, null);
	}
	public TcpClient(ExecutorService executor) {
		this(null, executor);
	}
	public TcpClient(EventBus bus, ExecutorService executor) {
		this.bus = bus;
		this.executor = executor;
	}
	@Override
	public TcpClient init() throws IOException, ServiceStateException {
		ServiceState.assertEquals(getState(), ServiceState.UNINITIALIZED);
		selector = Selector.open();
		state.set(ServiceState.INITIALIZED);
		return this;
	}
	@Override
	public TcpClient start() throws ServiceStateException {
		ServiceState.assertAndSet(this.state, ServiceState.INITIALIZED, ServiceState.STARTING);
		if (executor == null) {
			run();
		} else {
			executor.submit(this);
		}
		return this;
	}
	public TcpClient start(Consumer<? super TcpClient> initializer) throws IOException, ServiceStateException {
		ServiceState.assertAndSet(this.state, ServiceState.INITIALIZED, ServiceState.STARTING);
		initializer.accept(this);
		if (executor == null) {
			run();
		} else {
			executor.submit(this);
		}
		return this;
	}
	public TcpClient dispatchOn(EventBus bus) {
		this.bus = bus;
		return this;
	}
	public EventBus getBus() {
		return this.bus;
	}
	public TcpClient runOn(ExecutorService executor) {
		final ServiceState cstate = getState();
		if (cstate == ServiceState.UNINITIALIZED || cstate == ServiceState.INITIALIZED || cstate == ServiceState.STARTING)
			this.executor = executor;
		else
			throw new IllegalStateException("Expected: INITIALIZED or STARTING; State: "+cstate.name());	
		return this;
	}

	@Override
	public boolean isSecure() {
		return false;//not TLS
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Action onConnect(Consumer<TcpClientChannel> handler) {
		return new RegistrationCancelAction(this.bus.on($t("tcp.accept"), event -> handler.accept(((Event<TcpClientChannel>)event).getData())));
	}
	
	@Override
	public void run() {
		if (!state.compareAndSet(ServiceState.STARTING, ServiceState.RUNNING))
			throw new IllegalStateException("I was already running! (Expect: ServiceState#STARTING; was " + state.get() + ").");
		try {
			ServiceState cstate;//temporary state mirror for making life easy

			while ((cstate = getState()) == ServiceState.RUNNING || (cstate == ServiceState.STOPPING && channelMap.size() > 0)) {
				try {
					int nKeys = 0;//number of keys selected
					System.out.println("TCPc::Polling... ("+this.channelMap.size()+" open)");
					if ((cstate = getState()) == ServiceState.STOPPING)
						nKeys = selector.select(1000);//the server is stopping now, so ensure that this doesn't hang for as long.
					else
						nKeys = selector.select();//there are some open channels & the server isn't closing, so we don't have to check as much
					
					//Apply selector events
					while (!this.selectorUpdateQueue.isEmpty())
						selectorUpdateQueue.pop().accept(selector);
					
					if (nKeys > 0) {
						System.out.println("\tGot " + nKeys + " key(s)");
						Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
						while (keyIterator.hasNext()) {
							SelectionKey key = keyIterator.next();
							keyIterator.remove();
							System.out.println("\tKey : " + key.interestOps() + " |" + (key.isValid()?" Valid":"") + (key.isAcceptable()?" Acceptable":"") + (key.isReadable()?" Readable":"") + (key.isWritable()?" Writable":"") + (key.isConnectable()?" Connectable":""));
							if (!key.isValid())
								continue;
							
							if (key.isConnectable()) {
								this.connect(key);
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
	/**
	 * Accept an incoming connection.
	 * @param key selection key
	 * @throws IOException if there was a problem setting it up
	 */
	protected void connect(SelectionKey key) throws IOException {
		System.out.println("TCPc::Connecting...");
		SocketChannel socket = (SocketChannel) key.channel();
		
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
		long id = (Long)key.attachment();
		TcpClientChannel channel = channelMap.get(id);
		upgradeSocket(channel, socket, id);
		System.out.println("\tConnected to " + channel.getRemoteAddress().toString());
		System.out.println("\tID #"+id);
		
		//trigger handlers
		Event.Headers eventHeaders = new Event.Headers();
		eventHeaders.setOrigin("TcpClient@"+channel.getRemoteAddress().toString());
		bus.notify(Tuple.of("tcp.accept", channel.getConnectionID()),new Event<TcpClientChannel>(eventHeaders, channel));
		
		//queue for future I/O
		socket.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE, id);
		System.out.println("\tDone.");
	}
	protected void upgradeSocket(TcpClientChannel channel, SocketChannel socket, long id) throws IOException {
		//this method can be overridden by children of this class offering encryption & stuff
		try {
			socket.finishConnect();
		} catch (ConnectException e) {
			throw new ConnectException("Connection refused to " + channel.getRemoteAddress());
		}
	}
	/**
	 * Read buffer of data from socket
	 * @param key
	 * @throws IOException
	 */
	protected void read(SelectionKey key) throws IOException {
		//get socket & channel
		long id = (Long)key.attachment();
		System.out.println("TCPc::Reading #" + id + "...");
		TcpClientChannel channel = this.channelMap.get(id);
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
		System.out.println("TCPc::Read " + read + " bytes from #" + id);
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
			System.err.println("TCPc::Invalid key" + attachment);
			return;
		}
		long id = (Long)attachment;
		System.out.println("TCPc::Writing #" + id + "...");
		
		TcpClientChannel channel = this.channelMap.get(id);

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
	/**
	 * 
	 * @param addr
	 * @return
	 * @throws IOException if an I/O error occurs
	 */
	public TcpClientChannel open(SocketAddress addr) throws IOException {
		long id = nextId.incrementAndGet();
		TcpClientChannel channel = new TcpClientChannel(this, addr, id);
		this.channelMap.put(id, channel);
		return channel;
	}
	
	protected void doConnect(TcpClientChannel channel) throws IOException {
		channel.socket.connect(channel.getRemoteAddress());
	}
	protected void requireSelector(Consumer<Selector> action) {
		this.selectorUpdateQueue.push(action);
		this.selector.wakeup();
	}
}
package com.divisors.projectcuttlefish.httpserver.api.http;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.error.ServiceStateException;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

/**
 * Implementation of {@link HttpServer}
 * @author mailmindlin
 *
 */
@Deprecated
public class HttpServerImpl implements HttpServer {
	
	public static HttpServerFactory getFactory() {
		return (addr)->(new HttpServerImpl().listenOn(addr));
	}
	
	protected final ConcurrentHashMap<Long, HttpChannel> channelMap = new ConcurrentHashMap<>();
	protected final List<TcpServer> sources = new ArrayList<>();
	/**
	 * Executor that this server is run on. If {@link #listenOn(SocketAddress)} is called, the TcpServer
	 * created will also run on this executor.
	 */
	protected ExecutorService executor;
	/**
	 * Bus that events are dispatched on
	 */
	protected EventBus bus;
	/**
	 * Current state of the server.
	 * @see ServerState
	 */
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	public HttpServerImpl() {
		
	}
	public HttpServerImpl(EventBus bus) {
		this(bus, null);
	}
	public HttpServerImpl(ExecutorService executor) {
		this(null, executor);
	}
	public HttpServerImpl(EventBus bus, ExecutorService executor) {
		this.bus = bus;
		this.executor = executor;
	}
	/**
	 * Create a TcpServer on the given SocketAddress, and start it.
	 * @param addr address to listen on
	 * @return this
	 * @throws Exception 
	 * @throws IllegalStateException 
	 */
	public HttpServer listenOn(SocketAddress addr) throws ServiceStateException, IOException {
		System.out.println("HTTP::Listening on " + addr);
		if (ServiceState.assertNone(getState(), ServiceState.UNINITIALIZED, ServiceState.UNKNOWN, ServiceState.DESTROYED))
			new TcpServerImpl(addr)
				.init()
				.start((Consumer<TcpServer>)server->
					server
						.dispatchOn(this.bus)
						.runOn(this.executor)
						.onConnect(this::upgradeTcpChannel));
		return this;
	}
	/**
	 * Upgrades TcpChannel to a HttpChannel and dispatches a '<code>http.connect</code>' event.
	 * @param channel
	 */
	protected HttpChannel upgradeTcpChannel(TcpChannel channel) {
		System.out.println("HTTP::Attempting to upgrade channel from " + channel.getRemoteAddress() + " #" + channel.getConnectionID());
		return new HttpChannelImpl(channel, this);
	}
	@SuppressWarnings("unchecked")
	@Override
	public Action onConnect(Consumer<HttpChannel> handler) {
		return new RegistrationCancelAction(this.bus.on($t("http.connect"), event -> handler.accept(((Event<HttpChannel>)event).getData())));
	}
	@Override
	public ServiceState getState() {
		return this.state.get();
	}

	/**
	 * This HTTP server is NOT over SSL.
	 */
	@Override
	public boolean isSecure() {
		return false;
	}
	@Override
	public HttpServerImpl init() throws ServiceStateException {
		ServiceState.assertAndSet(this.state, ServiceState.UNINITIALIZED, ServiceState.INITIALIZED);
		return this;
	}
	
	@Override
	public HttpServerImpl start() throws IOException, ServiceStateException {
		return this.start((me)->{});
	}
	@Override
	public HttpServerImpl start(Consumer<? super HttpServer> initializer) throws IOException, ServiceStateException {
		ServiceState.assertAndSet(this.state, ServiceState.INITIALIZED, ServiceState.STARTING);
		initializer.accept(this);
		return this;
	}
	public HttpServerImpl runOn(ExecutorService executor) throws ServiceStateException {
		ServiceState.assertAny(getState(), ServiceState.UNINITIALIZED, ServiceState.INITIALIZED, ServiceState.STARTING);
		this.executor = executor;
		return this;
	}
	/**
	 * 
	 */
	@Override
	public HttpServerImpl dispatchOn(EventBus bus) {
		ServiceState.assertAny(getState(), ServiceState.UNINITIALIZED, ServiceState.INITIALIZED, ServiceState.STARTING);
		this.bus = bus;
		return this;
	}
	public EventBus getBus() {
		return this.bus;
	}
	@Override
	public void run() {
		//I don't need anything here
	}
	/**
	 * 
	 */
	@Override
	public boolean shutdown() {
		return false;
	}

	@Override
	public boolean shutdown(Duration timeout) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shutdownNow() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	@Override
	public Action onRequest(BiConsumer<HttpChannel, HttpRequest> handler) {
		return new RegistrationCancelAction(this.bus.on($t("http.request"), event -> {
			HttpRequest request = (HttpRequest) event.getData();
			long channelID = (Long)((Tuple)event.getKey()).get(1);
			HttpChannel channel= channelMap.get(channelID);
			handler.accept(channel, request);
		}));
	}
}

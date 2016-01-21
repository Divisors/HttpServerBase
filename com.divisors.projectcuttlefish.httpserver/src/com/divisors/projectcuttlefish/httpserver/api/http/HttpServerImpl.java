package com.divisors.projectcuttlefish.httpserver.api.http;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * Implementation of {@link HttpServer}
 * @author mailmindlin
 *
 */
public class HttpServerImpl implements HttpServer {
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
	/**
	 * Create a TcpServer on the given SocketAddress, and start it.
	 * @param addr address to listen on
	 * @return this
	 * @throws Exception 
	 * @throws IllegalStateException 
	 */
	public HttpServer listenOn(SocketAddress addr) throws IllegalStateException, Exception {
		System.out.println("HTTP::Listening on " + addr);
		TcpServerImpl tcp = new TcpServerImpl(addr);
		ServiceState state = this.state.get();
		if (state != ServiceState.DESTROYED && state != ServiceState.STOPPING)
			tcp
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
	protected void upgradeTcpChannel(TcpChannel channel) {
		System.out.println("HTTP::Attempting to upgrade channel from " + channel.getRemoteAddress() + " #" + channel.getConnectionID());
		new HttpChannelImpl(channel, this);
	}
	@SuppressWarnings("unchecked")
	@Override
	public HttpServerImpl onConnect(Predicate<HttpChannel> handler) {
		this.bus.on($t("http.connect"), (event)->{
			handler.test(((Event<HttpChannel>)event).getData());
		});
		return this;
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
	public HttpServerImpl init() throws Exception {
		this.state.compareAndSet(ServiceState.UNINITIALIZED, ServiceState.INITIALIZED);
		return this;
	}
	
	@Override
	public HttpServerImpl start() throws IOException, IllegalStateException {
		return this.start((me)->{});
	}
	@Override
	public HttpServerImpl start(Consumer<? super HttpServer> initializer) throws IOException, IllegalStateException {
		if (!this.state.compareAndSet(ServiceState.INITIALIZED, ServiceState.STARTING))
			throw new IllegalStateException("State was "+this.state.get().name()+"; expect: ServiceState#INITIALIZED");
		initializer.accept(this);
		return this;
	}
	public HttpServerImpl runOn(ExecutorService executor) {
		this.executor = executor;//TODO check state
		return this;
	}
	public HttpServerImpl dispatchOn(EventBus bus) {
		this.bus = bus;//TODO check state
		return this;
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
}

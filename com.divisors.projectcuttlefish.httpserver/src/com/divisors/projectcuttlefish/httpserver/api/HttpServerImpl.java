package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

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
	protected final AtomicReference<ServerState> state = new AtomicReference<>(ServerState.INITIALIZING);
	
	@Override
	public boolean isRunning() {
		ServerState state = this.state.get();
		return state == ServerState.RUNNING || state == ServerState.SHUTTING_DOWN;
	}
	@Override
	public void start(Consumer<? extends RunnableService> initiator) throws Exception {
		// TODO Auto-generated method stub

	}
	/**
	 * Create a TcpServer on the given SocketAddress, and start it.
	 * @param addr address to listen on
	 * @return this
	 * @throws IOException if there was a problem binding
	 */
	public HttpServer listenOn(SocketAddress addr) throws IOException {
		TcpServerImpl tcp = new TcpServerImpl(addr);
		if (isRunning())
			tcp.start(server->server.onConnect(this::upgradeTcpChannel));
		return this;
	}
	/**
	 * Upgrades TcpChannel to a HttpChannel and dispatches a '<code>http.connect</code>' event.
	 * @param channel
	 */
	protected void upgradeTcpChannel(TcpChannel channel) {
		//TODO finish
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
	public void init() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This HTTP server is NOT over SSL.
	 */
	@Override
	public boolean isSecure() {
		return false;
	}
	
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Server<HttpRequest, HttpResponse, HttpChannel> onConnect(Predicate<HttpChannel> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isShuttingDown() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * State of server
	 * @author mailmindlin
	 */
	public static enum ServerState {
		/**
		 * This state indicates that {@link HttpServer#start()} has not yet been called,
		 * and resources are still being acquired.
		 */
		INITIALIZING(false),
		/**
		 * After {@link HttpServer#start()} has been called, and before the server is running,
		 * this state indicates that future calls to start will be ignored (NOT throwing an error),
		 * and 
		 */
		STARTING(false),
		/**
		 * After the server has been started, and 
		 */
		RUNNING(true),
		/**
		 * 
		 */
		SHUTTING_DOWN(true),
		/**
		 * In this state, the server has stopped, but all resources are still retained (such as bound sockets),
		 * so future calls to {@link HttpServer#start()} should work.
		 */
		STOPPED(false),
		/**
		 * In this state, the server is stopped, and all resources have been released.
		 * The server cannot be restarted, and any calls to {@link HttpServer#start()} SHOULD throw an
		 * {@link IllegalStateException}.
		 */
		DESTROYED(false);
		/**
		 * Whether a server, in this state, should be considered to be running.
		 */
		public final boolean isRunning;
		private ServerState(boolean running) {
			this.isRunning = running;
		}
	}
}

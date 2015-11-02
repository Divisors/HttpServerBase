package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.HttpExchange;
import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.error.HttpError;
import com.divisors.projectcuttlefish.httpserver.api.error.HttpErrorHandler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public class HttpServerImpl implements HttpServer {
	protected final boolean isServerOrphan;
	final Server server;
	protected final AtomicReference<WeakReference<Thread>> currentThread = new AtomicReference<>(null);
	protected final LinkedList<HttpErrorHandler> errorHandlers = new LinkedList<>();
	protected final List<Entry<Predicate<HttpRequest>, HttpRequestHandler>> handlers = new LinkedList<>();
	
	public HttpServerImpl(Server server) {
		this.server = server;
		isServerOrphan=false;
	}
	public HttpServerImpl(InetSocketAddress addr) {
		this.server = new ServerImpl(addr);
		isServerOrphan = true;
	}

	/**
	 * Register a handler
	 */
	@Override
	public void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler) {
		handlers.add(new AbstractMap.SimpleImmutableEntry<>(requestFilter, handler));
	}

	@Override
	public void deregisterHandler(HttpRequestHandler handler) {
		handlers.removeIf((entry)->(entry.getValue().equals(handler)));
	}

	@Override
	public boolean isRunning() {
		synchronized (this.currentThread) {
			Thread t = this.currentThread.get().get();
			return t!=null && t.isAlive();
		}
	}
	
	@Override
	public void registerConnectionListener(BiConsumer<Socket, Server> handler) {
		server.registerConnectionListener(handler);
	}

	@Override
	public void deregisterConnectionListener(BiConsumer<Socket, Server> handler) {
		server.deregisterConnectionListener(handler);
	}

	@Override
	public void registerErrorHandler(HttpErrorHandler handler) {
		this.errorHandlers.add(handler);
	}
	@Override
	public void init() throws IOException {
		server.init();
		server.registerConnectionListener(this);
	}
	@Override
	public void destroy() throws IOException {
		server.deregisterConnectionListener(this);
	}
	@Override
	public boolean start() throws IOException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean start(ExecutorService executor) throws IOException {
		if (isRunning())
			return false;
		executor.submit(this);
		return true; 
	}
	@Override
	public boolean stop() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean stop(Duration timeout) throws IOException, InterruptedException {
		synchronized(this.currentThread) {

			WeakReference<Thread> thread = this.currentThread.get();
		}
		return false;
	}
	@Override
	public void run() {
		server.run();
		
	}
	@Override
	public InetSocketAddress getAddress() {
		return server.getAddress();
	}
	@Override
	public boolean isSSL() {
		return false;
	}
	@Override
	public boolean stopNow() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void accept(Socket socket, Server server) {
		
	}
	protected void onRequest(HttpRequest request, HttpExchange exchange) {
		this.handlers.stream()
			.filter((entry)->(entry.getKey().test(request)))
			.forEach((Consumer<Entry<Predicate<HttpRequest>,HttpRequestHandler>>)(entry)->(entry.getValue().accept(request, exchange)));
	}
	public void onError(HttpError error, HttpExchange exchange) {
		this.errorHandlers.forEach((handler)->{
			//TODO finish
		});
	}
}

package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.divisors.projectcuttlefish.httpserver.api.HttpExchange;
import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.error.HttpError;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;

import reactor.core.processor.RingBufferWorkProcessor;
import reactor.rx.Stream;
import reactor.rx.Streams;
import reactor.rx.broadcast.Broadcaster;

public class HttpServerImpl implements HttpServer {
	protected final boolean isServerOrphan;
	final TcpServer server;
	protected final AtomicReference<WeakReference<Thread>> currentThread = new AtomicReference<>(new WeakReference<>(null));
	protected final List<Entry<Predicate<HttpRequest>, HttpRequestHandler>> handlers = new LinkedList<>();
	Broadcaster<HttpExchange> onAccept;
	Broadcaster<HttpRequest> onRequest;
	AtomicBoolean running = new AtomicBoolean(false);
	public HttpServerImpl(TcpServer server) {
		this.server = server;
		isServerOrphan=false;
	}
	public HttpServerImpl(InetSocketAddress addr) {
		this.server = new TcpServerImpl(addr);
		isServerOrphan = true;
	}
	
	@Override
	public boolean isRunning() {
		synchronized (this.currentThread) {
			Thread t = this.currentThread.get().get();
			return t!=null && t.isAlive();
		}
	}
	
	@Override
	public void init() throws IOException {
		server.init();
		Processor<TcpConnection, TcpConnection> p=RingBufferWorkProcessor.create("Acceptor",32);
		Stream<TcpConnection> s = Streams.wrap(p);
		
	}
	@Override
	public void destroy() throws IOException {
		
	}
	@Override
	public boolean stop() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean stop(Duration timeout) throws IOException, InterruptedException {
		synchronized(this.currentThread) {
			Thread thread = this.currentThread.get().get();
			if (thread == null)
				return false;
			thread.interrupt();
			thread.join(timeout.toMillis());
			thread.stop();
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
	
	protected void onRequest(HttpRequest request, HttpExchange exchange) {
		this.handlers.stream()
			.filter((entry)->(entry.getKey().test(request)))
			.forEach((Consumer<Entry<Predicate<HttpRequest>,HttpRequestHandler>>)(entry)->(entry.getValue().accept(request, exchange)));
	}
	public void onError(HttpError error, HttpExchange exchange) {
		//TODO fin
	}
	@Override
	public void start() throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void start(ExecutorService executor) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		
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
	@Override
	public void onSubscribe(Subscription s) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNext(TcpConnection t) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onError(Throwable t) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onComplete() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void subscribe(Subscriber<? super HttpExchange> s) {
		// TODO Auto-generated method stub
		
	}
}

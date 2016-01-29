package com.divisors.projectcuttlefish.httpserver.client;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import reactor.bus.Event;
import reactor.bus.EventBus;

/**
 * HTTP client, loosely based on XMLHttpRequest
 * @author mailmindlin
 */
public class HttpClient implements Server<HttpResponse, HttpRequest, HttpClientChannel>{
	protected ExecutorService executor;
	protected EventBus bus;
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	protected TcpClient tcpClient;
	
	public HttpClient() throws IOException {
		this(new TcpClient());
	}
	public HttpClient(TcpClient tcp) {
		this(tcp, null, null);
	}
	public HttpClient(ExecutorService executor) {
		this(new TcpClient(executor), null, executor);
	}
	public HttpClient(EventBus bus) {
		this(new TcpClient(bus), bus, null);
	}
	public HttpClient(EventBus bus, ExecutorService executor) {
		this(new TcpClient(bus, executor), bus, executor);
	}
	public HttpClient(TcpClient tcp, EventBus bus, ExecutorService executor) {
		this.tcpClient = tcp;
		this.bus = bus;
		this.executor = executor;
	}
	public HttpClient runOn(ExecutorService executor) {
		this.executor = executor;
		return this;
	}
	@Override
	public HttpClient init() throws Exception {
		if (!this.state.compareAndSet(ServiceState.UNINITIALIZED, ServiceState.INITIALIZED))
			throw new IllegalStateException();//TODO add msg
		tcpClient.init();
		return this;
	}
	@Override
	public HttpClient start() throws Exception {
		if (!this.state.compareAndSet(ServiceState.INITIALIZED, ServiceState.STARTING))
			throw new IllegalStateException();//TODO add msg
		tcpClient.start();
		return this;
	}
	@Override
	public boolean isSecure() {
		return false;
	}
	@Override
	public ServiceState getState() {
		return this.state.get();
	}
	@Override
	public void run() {
		tcpClient.run();
	}
	public HttpClientChannel open(SocketAddress addr) throws IOException {
		return new HttpClientChannel(this, tcpClient.open(addr));
	}
	protected void doConnect(HttpClientChannel channel) throws IOException {
		channel.tcp.connect();
	}
	@Override
	@SuppressWarnings("unchecked")
	public Action onConnect(Consumer<HttpClientChannel> handler) {
		return new RegistrationCancelAction(bus.on($t("http.accept"), event->handler.accept(((Event<HttpClientChannel>)event).getData())));
		
	}
	@Override
	public boolean shutdown() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean shutdown(Duration timeout) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean shutdownNow() throws Exception {
		//TODO release channels
		return tcpClient.shutdownNow();
	}
	@Override
	public void destroy() throws RuntimeException {
		this.bus = null;
		this.tcpClient.destroy();
		this.tcpClient = null;
		this.executor.shutdownNow();
		this.executor = null;
	}
}

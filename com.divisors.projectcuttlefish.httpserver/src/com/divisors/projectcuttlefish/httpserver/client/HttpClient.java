package com.divisors.projectcuttlefish.httpserver.client;

import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.RunnableService;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestBuilder;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

import reactor.bus.EventBus;

/**
 * HTTP client, loosely based on XMLHttpRequest
 * @author mailmindlin
 */
public class HttpClient implements Server<HttpResponse, HttpRequest, HttpClientChannel>{
	protected Executor executor;
	protected EventBus bus;
	protected final AtomicReference<HttpClientReadyState> state = new AtomicReference<>(HttpClientReadyState.UNSENT);
	public HttpClient() {
	}
	public HttpClient runOn(Executor executor) {
		this.executor = executor;
		return this;
	}
	public void abort() {
		
	}
	void open(String method, URL url) {
	}
	void open(String method, String url, boolean async) {
		
	}
	void open(String method, String url, boolean async, String user) {
		
	}
	void open(String method, String url, boolean async, String user, String password) {
		
	}
	void overrideMimeType(String mime) {
		
	}
	void send() {
		
	}
	void send(ByteBuffer data) {
		
	}
	void send(Blob data) {
		
	}
	void send(String data) {
		
	}
	void send(JSONObject data) {
		
	}
	void setRequestHeader(String header, String value) {
		
	}
	@Override
	public RunnableService init() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public RunnableService start() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void destroy() throws RuntimeException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Action onConnect(Consumer<HttpClientChannel> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public ServiceState getState() {
		// TODO Auto-generated method stub
		return null;
	}
	
}

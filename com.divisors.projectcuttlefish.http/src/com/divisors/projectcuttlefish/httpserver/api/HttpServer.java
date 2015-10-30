package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetAddress;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public interface HttpServer extends Runnable {
	void init() throws IOException;
	void run();
	void shutdown() throws IOException, InterruptedException, IllegalStateException;
	void destroy() throws IOException;
	
	default void registerHandler(String path, HttpRequestHandler handler) {
		registerHandler((a)->(a.getPath().equalsIgnoreCase(path)), handler);
	}
	void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler);
	void deregisterHandler(HttpRequestHandler handler);
	
	boolean isRunning();
	int getPort();
	InetAddress getAddress();
	boolean isSSL();
	
	
}

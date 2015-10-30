package com.divisors.projectcuttlefish.httpserver.api;

import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.error.HttpErrorHandler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public interface HttpServer extends Server, Runnable {
	
	default void registerHandler(String path, HttpRequestHandler handler) {
		registerHandler((a)->(a.getPath().equalsIgnoreCase(path)), handler);
	}
	void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler);
	void deregisterHandler(HttpRequestHandler handler);
	
	void registerErrorHandler(HttpErrorHandler handler);
	
	boolean isRunning();
	default int getPort() {
		return getAddress().getPort();
	}	
	
}

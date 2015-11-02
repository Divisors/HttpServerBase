package com.divisors.projectcuttlefish.httpserver.api;

import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.error.HttpErrorHandler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public interface HttpServer extends Server, RunnableService, BiConsumer<Socket, Server> {
	
	default void registerHandler(HttpRequestHandler handler) {
		registerHandler((a)->(true),handler);
	}
	default void registerHandler(String path, HttpRequestHandler handler) {
		registerHandler((a)->(a.getPath().equalsIgnoreCase(path)), handler);
	}
	void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler);
	void deregisterHandler(HttpRequestHandler handler);
	
	void registerErrorHandler(HttpErrorHandler handler);
	
	/**
	 * Get the port number that this server is connected to (or -1 if not connected to any port)
	 * @return port connected to
	 * @see #getAddress()
	 */
	default int getPort() {
		return getAddress().getPort();
	}
	
}

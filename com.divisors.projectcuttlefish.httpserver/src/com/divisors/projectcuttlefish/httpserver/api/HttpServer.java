package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.error.HttpErrorHandler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;
import com.divisors.projectcuttlefish.httpserver.api.tcp.Connection;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;

public interface HttpServer extends RunnableService, BiConsumer<Connection, TcpServer> {
	
	public boolean stop() throws IOException, InterruptedException;
	public boolean stop(Duration timeout) throws IOException, InterruptedException;
	
	default void registerHandler(HttpRequestHandler handler) {
		registerHandler((a)->(true),handler);
	}
	default void registerHandler(String path, HttpRequestHandler handler) {
		registerHandler((a)->(a.getPath().equalsIgnoreCase(path)), handler);
	}
	void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler);
	void deregisterHandler(HttpRequestHandler handler);
	
	void registerErrorHandler(HttpErrorHandler handler);
	
	InetSocketAddress getAddress();
	/**
	 * Get the port number that this server is connected to (or -1 if not connected to any port)
	 * @return port connected to
	 * @see #getAddress()
	 */
	default int getPort() {
		return getAddress().getPort();
	}
	
	boolean isSSL();  
}

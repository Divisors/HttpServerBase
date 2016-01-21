package com.divisors.projectcuttlefish.httpserver.api.http;

import java.io.IOException;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

/**
 * Server for responding to HTTP requests. Usually upgrades one or more
 * {@link com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer TcpServer}'s.
 * @author mailmindlin
 * @see HttpServerFactory
 */
public interface HttpServer extends Server<HttpRequest, HttpResponse, HttpChannel> {
	/**
	 * Start server with initializer
	 * @param initializer function to be run immediately before server start
	 * @return self
	 * @throws IOException
	 * @throws IllegalStateException if this server is currently running
	 * @see #start()
	 */
	HttpServer start(Consumer<? super HttpServer> initializer) throws IOException, IllegalStateException;
	/**
	 * Start server with nop initializer
	 * @return self
	 * @throws IOException
	 * @throws IllegalStateException if this server is currently running
	 * @see #start(Consumer)
	 */
	@Override
	HttpServer start() throws IOException, IllegalStateException;
	
	@Override
	HttpServer init() throws Exception;
}

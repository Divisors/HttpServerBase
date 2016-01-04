package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Factory for producing {@link HttpServer}s. 
 * @author mailmindlin
 */
public interface HttpServerFactory {
	/**
	 * Create a http server initially bound to the given port on localhost.
	 * If not overriden, syntatically equivalent to {@link #createServer(String,int) createServer("localhost",port)}.
	 * @param port port on localhost to bind to
	 * @return server created
	 * @throws IOException if there was a problem with binding.
	 */
	default HttpServer createServer(int port) throws IOException {
		return createServer("localhost", port);
	}
	/**
	 * Create
	 * @param host hostname to be bound to
	 * @param port port number on host
	 * @return server
	 * @throws IOException upon a binding problem
	 */
	default HttpServer createServer(String host, int port) throws IOException {
		return createServer(new InetSocketAddress(host, port));
	}
	/**
	 * Create a HTTP server from a SocketAddress
	 * @param addr address to create from
	 * @return server
	 * @throws IOException upon a binding problem
	 */
	public HttpServer createServer(SocketAddress addr) throws IOException;
}

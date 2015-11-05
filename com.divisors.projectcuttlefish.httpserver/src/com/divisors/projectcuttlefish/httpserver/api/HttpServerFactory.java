package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface HttpServerFactory {
	default HttpServer createServer(int port) throws IOException {
		return createServer(new InetSocketAddress(port));
	}
	default HttpServer createServer(String host, int port) throws IOException {
		return createServer(new InetSocketAddress(host, port));
	}
	public HttpServer createServer(InetSocketAddress addr) throws IOException;
}

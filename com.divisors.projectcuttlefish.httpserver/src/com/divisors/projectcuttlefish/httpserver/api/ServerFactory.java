package com.divisors.projectcuttlefish.httpserver.api;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface ServerFactory {
	default Server createServer(int port) {
		return createServer(new InetSocketAddress(port));
	}
	default Server createServer(String host, int port) {
		return createServer(new InetSocketAddress(host, port));
	}
	Server createServer(InetSocketAddress addr);
}

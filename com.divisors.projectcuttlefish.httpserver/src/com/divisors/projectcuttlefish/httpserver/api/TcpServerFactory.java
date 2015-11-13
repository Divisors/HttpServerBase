package com.divisors.projectcuttlefish.httpserver.api;

import java.net.InetSocketAddress;

@FunctionalInterface
public interface TcpServerFactory {
	default TcpServer createServer(int port) {
		return createServer(new InetSocketAddress(port));
	}
	default TcpServer createServer(String host, int port) {
		return createServer(new InetSocketAddress(host, port));
	}
	TcpServer createServer(InetSocketAddress addr);
}

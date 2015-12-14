package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class TcpServerFactory {
	public static TcpServer createServer(int port) throws IOException {
		return createServer(new InetSocketAddress(port));
	}
	public static  TcpServer createServer(String host, int port) throws IOException {
		return createServer(new InetSocketAddress(host, port));
	}
	public static TcpServer createServer(InetSocketAddress addr) throws IOException {
		return new TcpServerImpl(addr);
	}
}

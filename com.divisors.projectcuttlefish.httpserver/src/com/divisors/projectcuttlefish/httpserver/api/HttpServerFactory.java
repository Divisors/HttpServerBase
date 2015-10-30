package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public interface HttpServerFactory {
	public HttpServer createServer(int port) throws IOException;
	public HttpServer createServer(int port, InetAddress address) throws IOException;
	public HttpServer createServer(ServerSocket s) throws IOException;
}

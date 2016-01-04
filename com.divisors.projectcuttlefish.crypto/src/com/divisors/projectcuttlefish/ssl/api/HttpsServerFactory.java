package com.divisors.projectcuttlefish.ssl.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;

/**
 * 
 * @author mailmindlin
 *
 */
public interface HttpsServerFactory extends HttpServerFactory {
	public HttpsServer createServer(int port) throws IOException;
	public HttpsServer createServer(int port, InetAddress address) throws IOException;
	public HttpsServer createServer(ServerSocket s) throws IOException;
}

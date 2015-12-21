package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Factory for creating {@link TcpServer}'s
 * @author mailmindlin
 * @see TcpServer
 * @see TcpServerImpl
 */
public final class TcpServerFactory {
	protected static final TcpServerFactory INSTANCE = new TcpServerFactory();
	public static TcpServerFactory getInstance() {
		return INSTANCE;
	}
	/**
	 * Create tcp server bound to the given port on localhost.
	 * @param port port for server to be bound to
	 * @return server created
	 * @throws IOException if there was a problem binding the port
	 */
	public TcpServer createServer(int port) throws IOException {
		return createServer("localhost", port);
	}
	public TcpServer createServer(String host, int port) throws IOException {
		return createServer(new InetSocketAddress(host, port));
	}
	public TcpServer createServer(SocketAddress addr) throws IOException {
		return new TcpServerImpl(addr);
	}
}

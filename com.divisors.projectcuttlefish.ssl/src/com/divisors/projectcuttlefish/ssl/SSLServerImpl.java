package com.divisors.projectcuttlefish.ssl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLServerImpl extends com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl{

	public SSLServerImpl(int port) throws IOException {
		super(port);
	}
	
	public SSLServerImpl(InetSocketAddress address) throws IOException {
		super(address);
	}

	protected void promoteSocket(Socket socket, String host, int port, boolean client) throws IOException {
		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket sslSocket = (SSLSocket) factory.createSocket(socket, host, port, true);
		sslSocket.setUseClientMode(false);
	}
	@Override
	public boolean isSSL() {
		return true;
	}
}

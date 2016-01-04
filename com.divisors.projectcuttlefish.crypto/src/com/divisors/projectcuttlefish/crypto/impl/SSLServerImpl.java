package com.divisors.projectcuttlefish.crypto.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.divisors.projectcuttlefish.crypto.api.SSLServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannelImpl;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

/**
 * 
 * @author mailmindlin
 *
 */
public class SSLServerImpl extends TcpServerImpl implements SSLServer {
	/**
	 * 
	 */
	private SSLContext context;
	public SSLServerImpl(SocketAddress addr) throws IOException {
		super(addr);
	}

	public SSLServerImpl(int port) throws IOException {
		super(port);
	}
	public SSLServerImpl initSSL(String protocol) throws NoSuchAlgorithmException {
		this.context = SSLContext.getInstance(protocol);
		return this;
	}
	@Override
	public SSLServerImpl start() {
		if (context==null)
			throw new IllegalStateException("SSL not initialized");
		super.start();
		return this;
	}
	@Override
	protected TcpChannelImpl upgradeSocket(SocketChannel socket, long id) {
		SSLEngine engine = context.createSSLEngine();
		return new SSLChannelImpl(engine, this, socket, id);
	}
	@Override
	public boolean isSecure() {
//		SSLEngine engine = new SSLEngine();
		return true;
	}
	
}

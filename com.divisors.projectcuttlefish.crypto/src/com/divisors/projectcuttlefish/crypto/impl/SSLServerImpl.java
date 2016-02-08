package com.divisors.projectcuttlefish.crypto.impl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import com.divisors.projectcuttlefish.crypto.api.SSLServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannelImpl;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

import reactor.bus.EventBus;

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
	public SSLServerImpl(int port) throws IOException {
		super(port);
	}
	public SSLServerImpl(SocketAddress addr) throws IOException {
		super(addr);
	}
	public SSLServerImpl(ExecutorService executor, SocketAddress addr) {
		super(executor, addr);
	}
	public SSLServerImpl(EventBus bus, SocketAddress addr) {
		super(bus, addr);
	}
	public SSLServerImpl(EventBus bus, ExecutorService executor, SocketAddress addr) {
		super(bus, executor, addr);
	}
	@Override
	public SSLServerImpl init() throws IllegalStateException, IOException {
		super.init();
		if (this.context == null) {
			try {
				this.context = SSLContext.getDefault();
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
		return this;
	}
	public SSLServerImpl initSSL(String protocol) throws NoSuchAlgorithmException {
		this.context = SSLContext.getInstance(protocol);
		return this;
	}
	@Override
	public SSLServerImpl start() {
		if (this.context==null)
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
		return true;
	}
	
}

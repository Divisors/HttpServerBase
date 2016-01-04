package com.divisors.projectcuttlefish.crypto.impl;

import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannelImpl;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

/**
 * 
 * @author mailmindlin
 *
 */
public class SSLChannelImpl extends TcpChannelImpl {
	private final SSLEngine engine;
	public SSLChannelImpl(SSLEngine engine, TcpServerImpl server, SocketChannel socket, final long id) {
		super(server, socket, id);
		this.engine = engine;
	}
}

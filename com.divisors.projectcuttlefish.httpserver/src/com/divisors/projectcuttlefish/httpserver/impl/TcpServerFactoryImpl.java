package com.divisors.projectcuttlefish.httpserver.impl;

import java.net.InetSocketAddress;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerFactory;

public class TcpServerFactoryImpl implements TcpServerFactory {
	
	@Override
	public TcpServer createServer(InetSocketAddress addr) {
		return new TcpServerImpl(addr);
	}
	
}

package com.divisors.projectcuttlefish.httpserver.impl;

import java.net.InetSocketAddress;

import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.ServerFactory;

public class ServerFactoryImpl implements ServerFactory {
	
	@Override
	public Server createServer(InetSocketAddress addr) {
		return new ServerImpl(addr);
	}
	
}

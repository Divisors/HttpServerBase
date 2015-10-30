package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;

public class HttpServerFactoryImpl implements HttpServerFactory {

	@Override
	public HttpServer createServer(InetSocketAddress addr) throws IOException {
		return new HttpServerImpl(addr);
	}
	
}

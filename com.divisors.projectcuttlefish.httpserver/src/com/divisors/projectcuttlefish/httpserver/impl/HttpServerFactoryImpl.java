package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;

public class HttpServerFactoryImpl implements HttpServerFactory {

	@Override
	public HttpServer createServer(int port) throws IOException {
		return null;
	}

	@Override
	public HttpServer createServer(ServerSocket s) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpServer createServer(int port, InetAddress address) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

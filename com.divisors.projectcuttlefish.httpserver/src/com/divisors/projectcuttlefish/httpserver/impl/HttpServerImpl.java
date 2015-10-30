package com.divisors.projectcuttlefish.httpserver.impl;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.error.HttpErrorHandler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public class HttpServerImpl extends ServerImpl implements HttpServer {

	public HttpServerImpl(int port) {
		super(port);
	}
	public HttpServerImpl(InetSocketAddress addr) {
		super(addr);
	}

	@Override
	public void registerHandler(Predicate<HttpRequest> requestFilter, HttpRequestHandler handler) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deregisterHandler(HttpRequestHandler handler) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void registerConnectionListener(BiConsumer<Socket, Server> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregisterConnectionListener(BiConsumer<Socket, Server> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerErrorHandler(HttpErrorHandler handler) {
		// TODO Auto-generated method stub
		
	}

}

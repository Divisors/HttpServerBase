package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestHandler;

public class HttpServerImpl implements HttpServer {
	protected final int port;
	protected ServerSocketChannel serverSocketChannel;

	public HttpServerImpl(int port) {
		this.port = port;
	}

	@Override
	public void init() throws IOException {
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.socket().bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
	}

	@Override
	public void run() {
		if (serverSocketChannel == null)
			throw new IllegalStateException("serverSocketChannel not initialized");
		while (true) {
			try {
				SocketChannel socketChannel = serverSocketChannel.accept();
				if (socketChannel != null) {
					
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() throws IOException {
		serverSocketChannel.close();
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
	public int getPort() {
		return port;
	}

	@Override
	public InetAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSSL() {
		// TODO Auto-generated method stub
		return false;
	}

}

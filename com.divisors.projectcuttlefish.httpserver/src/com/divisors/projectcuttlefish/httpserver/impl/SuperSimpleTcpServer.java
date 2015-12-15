package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

import reactor.io.net.NetStreams;
import reactor.io.net.tcp.ReactorTcpServer;
import reactor.io.net.tcp.support.SocketUtils;
import reactor.rx.Streams;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;

public class SuperSimpleTcpServer implements TcpServer {
	int port;
	ReactorTcpServer<?, ?> server;
	@Override
	public boolean shutdown() throws Exception {
		return false;
	}

	@Override
	public boolean shutdown(Duration timeout) throws Exception {
		return false;
	}

	@Override
	public boolean shutdownNow() throws Exception {
		return false;
	}

	@Override
	public void init() throws IOException {
		port = SocketUtils.findAvailableTcpPort();
		server = NetStreams.tcpServer(s -> s.listen("localhost", port));
	}

	@Override
	public void destroy() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() throws IOException, IllegalStateException {
		server.start(channel -> {
			
			return Streams.never();
		});
	}

	@Override
	public void start(ExecutorService executor) throws IOException, IllegalStateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean stop() throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(Duration timeout) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public InetSocketAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerConnectionListener(BiConsumer<TcpConnection, TcpServer> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregisterConnectionListener(BiConsumer<TcpConnection, TcpServer> handler) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSSL() {
		// TODO Auto-generated method stub
		return false;
	}

}

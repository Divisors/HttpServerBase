package com.divisors.projectcuttlefish.httpserver.api;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.Executor;

public abstract class Server implements Runnable {
	protected final InetSocketAddress address;
	
	public Server(int port) {
		address = new InetSocketAddress(port);
	}
	
	public int getPort() {
		return address.getPort();
	}
	
	public void init() {
		
	}
	
	public void destroy() {
		
	}
	
	public void start() {
		
	}
	
	public void start(Executor executor) {
		
	}
	
	public void stop() {
		
	}
	public void stop(Duration timeout) {
		
	}
	@Override
	public void run() {
		
	}
}

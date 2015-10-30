package com.divisors.projectcuttlefish.httpserver.api;

import java.net.InetSocketAddress;

public abstract class Server implements Runnable {
	protected final InetSocketAddress address;
	
	public Server(int port) {
		address = new InetSocketAddress(port);
	}
	public int getPort() {
		return address.getPort();
	}
}

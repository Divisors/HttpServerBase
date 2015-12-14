package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;

import org.reactivestreams.Processor;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;

public interface HttpServer extends RunnableService, Processor<TcpChannel, HttpExchange> {
	
	public boolean stop() throws IOException, InterruptedException;
	public boolean stop(Duration timeout) throws IOException, InterruptedException;
	
	
	
	InetSocketAddress getAddress();
	/**
	 * Get the port number that this server is connected to (or -1 if not connected to any port)
	 * @return port connected to
	 * @see #getAddress()
	 */
	default int getPort() {
		return getAddress().getPort();
	}
	
	boolean isSSL();  
}

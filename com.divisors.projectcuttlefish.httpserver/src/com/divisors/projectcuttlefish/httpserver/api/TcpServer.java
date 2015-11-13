package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

/**
 * Generic server interface.
 * @author mailmindlin
 *
 */
public interface TcpServer extends RunnableService {
	@Override
	void init() throws IOException;
	
	@Override
	void destroy() throws IOException;
	
	@Override
	void start() throws IOException, IllegalStateException;
	@Override
	void start(ExecutorService executor) throws IOException, IllegalStateException;
	
	@Override
	void run();
	
	public boolean stop() throws IOException, InterruptedException;
	public boolean stop(Duration timeout) throws IOException, InterruptedException;
	
	InetSocketAddress getAddress();
	
	boolean isRunning();
	
	void registerConnectionListener(BiConsumer<Connection, TcpServer> handler);
	void deregisterConnectionListener(BiConsumer<Connection, TcpServer> handler);
	
	boolean isSSL();

}

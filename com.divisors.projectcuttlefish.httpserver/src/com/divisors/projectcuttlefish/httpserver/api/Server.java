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
public interface Server extends RunnableService {
	void init() throws IOException;
	
	void destroy() throws IOException;
	
	void start() throws IOException, IllegalStateException;
	void start(ExecutorService executor) throws IOException, IllegalStateException;
	
	boolean stop() throws IOException, InterruptedException;
	boolean stop(Duration timeout)  throws IOException, InterruptedException;
	
	@Override
	void run();
	
	InetSocketAddress getAddress();
	
	boolean isRunning();
	
	void registerConnectionListener(BiConsumer<Connection, Server> handler);
	void deregisterConnectionListener(BiConsumer<Connection, Server> handler);
	
	boolean isSSL();

}

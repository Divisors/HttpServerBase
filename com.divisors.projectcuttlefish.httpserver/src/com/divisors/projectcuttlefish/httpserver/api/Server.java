package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;

public interface Server extends RunnableService {
	void init() throws IOException;
	
	void destroy() throws IOException;
	
	boolean start() throws IOException;
	boolean start(ExecutorService executor) throws IOException;
	
	boolean stop() throws IOException, InterruptedException;
	boolean stop(Duration timeout)  throws IOException, InterruptedException;
	
	@Override
	void run();
	
	InetSocketAddress getAddress();
	
	boolean isRunning();
	
	void registerConnectionListener(BiConsumer<Socket, Server> handler);
	void deregisterConnectionListener(BiConsumer<Socket, Server> handler);
	
	boolean isSSL();

}

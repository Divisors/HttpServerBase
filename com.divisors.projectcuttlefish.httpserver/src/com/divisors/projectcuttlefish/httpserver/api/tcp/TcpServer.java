package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Generic server interface.
 * @author mailmindlin
 *
 */
public interface TcpServer {
	
	TcpServer start(Consumer<? super TcpServer> initializer) throws IOException, IllegalStateException;
	default TcpServer start() throws IOException, IllegalStateException {
		return this.start((x)->{});
	}
	
	InetSocketAddress getAddress();
	
	boolean isRunning();
	
	boolean isSSL();
	
	TcpServer onConnect(BiConsumer<TcpServer, TcpChannel> handler);
	
	public boolean stop() throws IOException, InterruptedException;
	public boolean stop(Duration timeout) throws IOException, InterruptedException;
}

package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Generic server interface.
 * @author mailmindlin
 * @see TcpServerImpl
 */
public interface TcpServer {
	
	/**
	 * Start server with initializer
	 * @param initializer function to be run immediately before server start
	 * @return self
	 * @throws IOException
	 * @throws IllegalStateException if this server is currently running
	 * @see #start()
	 */
	TcpServer start(Consumer<? super TcpServer> initializer) throws IOException, IllegalStateException;
	/**
	 * Start server with nop initializer
	 * @return self
	 * @throws IOException
	 * @throws IllegalStateException if this server is currently running
	 * @see #start(Consumer)
	 */
	default TcpServer start() throws IOException, IllegalStateException {
		return this.start((x)->{});
	}
	
	/**
	 * Get the address that this server is bound to
	 * @return address
	 */
	SocketAddress getAddress();
	
	/**
	 * Whether this server is currently bound to its address and recieving requests
	 * @return flag
	 */
	boolean isRunning();
	
	/**
	 * Whether this server's communications are encrypted via SSL
	 * @return flag
	 */
	boolean isSSL();
	
	TcpServer onConnect(Consumer<TcpChannel> handler);
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean stop() throws IOException, InterruptedException;
	/**
	 * Attempts to stop the server.
	 * <br/>
	 * Will time out, and gracefully fail
	 * @param timeout
	 * @return success in stopping the server
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean stop(Duration timeout) throws IOException, InterruptedException;
}

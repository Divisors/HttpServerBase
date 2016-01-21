package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Server;

import reactor.bus.EventBus;

/**
 * Generic server interface.
 * @author mailmindlin
 * @see TcpServerImpl
 */
public interface TcpServer extends Server<ByteBuffer, ByteBuffer, TcpChannel>{
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
	@Override
	default TcpServer start() throws IOException, IllegalStateException {
		return this.start((x)->{});
	}
	
	/**
	 * Get the address that this server is bound to
	 * @return address
	 */
	SocketAddress getAddress();
	
	/**
	 * Whether this server's communications are encrypted via SSL/TLS.
	 * @return that
	 * @see Server#isSecure()
	 */
	@Override
	boolean isSecure();
	
	/**
	 * @param handler
	 * @return self
	 */
	@Override
	TcpServer onConnect(Consumer<TcpChannel> handler);
	/**
	 * 
	 * @param processor
	 * @return self
	 */
	TcpServer dispatchOn(EventBus bus);
	/**
	 * 
	 * @param executor
	 * @return self
	 */
	TcpServer runOn(ExecutorService executor);
	
	@Override
	TcpServer init() throws Exception;
}

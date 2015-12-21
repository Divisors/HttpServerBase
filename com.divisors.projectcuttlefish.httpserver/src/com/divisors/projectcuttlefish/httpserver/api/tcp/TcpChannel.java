package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

import com.divisors.projectcuttlefish.httpserver.api.Channel;

import reactor.fn.Consumer;

/**
 * A connection between a server and a client. Methods are (mostly) non-blocking,
 * and queue-based.
 * TODO add prioritization for messages
 * @author mailmindlin
 * 
 * @see TcpChannelImpl
 *
 */
public interface TcpChannel extends Channel<ByteBuffer, ByteBuffer> {
	/**
	 * Get connection id. Connection ids should be unique to each channel from a single server.
	 * @return connection id
	 */
	long getConnectionID();
	TcpServer getServer();
	/**
	 * Get address of the other end of the socket
	 * @return address remote address of the channel, or null if an exception would have been thrown
	 * @see java.nio.channels.SocketChannel#getRemoteAddress()
	 */
	SocketAddress getRemoteAddress();
	/**
	 * Queue buffer for writing to the tcp socket
	 */
	@Override
	TcpChannel write(ByteBuffer data);
	/**
	 * Add read handler
	 */
	@Override
	TcpChannel onRead(Consumer<ByteBuffer> handler);
}
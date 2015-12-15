package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.SocketAddress;

import com.divisors.projectcuttlefish.httpserver.api.Channel;

/**
 * A connection between a server and a client. Methods are (mostly) non-blocking,
 * and queue-based.
 * TODO add prioritization for messages
 * @author mailmindlin
 * 
 * @see com.projectcuttlefish.httpserver.impl.ConnectionImpl
 *
 */
public interface TcpChannel extends Channel<Buffer, Buffer> {	
	long getConnectionID();
	void setConnectionID(long id);
	TcpServer getServer();
	SocketAddress getRemoteAddress() throws IOException;
}
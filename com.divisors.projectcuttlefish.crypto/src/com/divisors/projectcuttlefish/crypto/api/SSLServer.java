package com.divisors.projectcuttlefish.crypto.api;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;

/**
 * 
 * @author mailmindlin
 *
 */
public interface SSLServer extends TcpServer {
	@Override
	default boolean isSecure() {
		return true;
	}
}

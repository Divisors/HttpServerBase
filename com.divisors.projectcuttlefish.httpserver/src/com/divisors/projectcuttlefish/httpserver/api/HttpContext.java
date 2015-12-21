package com.divisors.projectcuttlefish.httpserver.api;

import java.net.SocketAddress;

/**
 * Provides information about the current http channel
 * @author mailmindlin
 */
public interface HttpContext {
	default boolean isSSL() {
		return false;
	}
	/**
	 * Get address bound to
	 * @return address, or null if not available
	 */
	SocketAddress getAddress();
	/**
	 * Whether this channel is still connected to something else
	 * @return
	 */
	boolean isOpen();
	/**
	 * Set k/v property on the channel
	 * @param key key
	 * @param value value
	 * @return self
	 */
	HttpContext setProperty(String key, Object value);
	/**
	 * Get k/v property set on this channel
	 * @param key key
	 * @return value, or null if not set
	 */
	Object getProperty(String key);
}

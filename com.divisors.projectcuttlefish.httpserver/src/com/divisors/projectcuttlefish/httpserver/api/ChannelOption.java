package com.divisors.projectcuttlefish.httpserver.api;

/**
 * An option to set 
 * @author mailmindlin
 * @see Channel
 */
public interface ChannelOption<E> {
	default boolean unsetWithValue(E value) {
		return false;
	}
}

package com.divisors.projectcuttlefish.httpserver.api;

/**
 * Generic server typpe
 * @author mailmindlin
 *
 * @param <IN> input type (type read from this server)
 * @param <OUT> output type (type written to this server)
 * @param <CHANNEL> channel type
 */
public interface Server<IN, OUT, CHANNEL extends Channel<IN,OUT>> {
	/**
	 * Whether this server is encrypting connections with ssl. Only returns true if
	 * ALL sockets that this server is listening to are using ssl.
	 * @return whether ssl is used
	 */
	boolean isSSL();
}

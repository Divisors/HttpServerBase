package com.divisors.projectcuttlefish.httpserver.api;

import reactor.fn.Consumer;

/**
 * Channel
 * @author mailmindlin
 *
 * @param <IN> input type (type to receive)
 * @param <OUT> output type (type to write)
 */
public interface Channel<IN,OUT> extends AutoCloseable {
	/**
	 * 
	 * @param data
	 * @return self
	 */
	Channel<IN, OUT> write(OUT data);
	/**
	 * Read handler.
	 * @param handler
	 * @return self
	 */
	Channel<IN, OUT> onRead(Consumer<IN> handler);
	/**
	 * Convert to another channel type
	 * @param codec codec to map with
	 * @return new channel
	 */
	<X,Y> Channel<X,Y> map(Codec<IN,OUT,X,Y> codec);
	
	/**
	 * Whether this channel is currently open for I/O
	 * @return flag
	 */
	boolean isOpen();
	
}

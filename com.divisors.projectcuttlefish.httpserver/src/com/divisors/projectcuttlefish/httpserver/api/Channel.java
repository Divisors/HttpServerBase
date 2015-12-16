package com.divisors.projectcuttlefish.httpserver.api;

import java.util.function.Consumer;

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
	<X,Y> Channel<X,Y> map(Codec<IN,X,OUT,Y> codec);
	
	boolean isOpen();
	
}

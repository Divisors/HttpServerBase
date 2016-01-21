package com.divisors.projectcuttlefish.httpserver.api.response;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public interface HttpResponsePayload extends Closeable {
	public static HttpResponsePayload wrap(ByteBuffer buffer) {
		return new HttpResponseByteBufferPayload(buffer);
	}
	/**
	 * Get size of payload data (not yet written), in bytes. MUST be positive.
	 * @return size
	 */
	long remaining();
	/**
	 * Set output 
	 * @param writer
	 */
	void drainTo(Consumer<ByteBuffer> writer);
	/**
	 * 
	 * @return
	 */
	boolean isDone();
	@Override
	void close();
}

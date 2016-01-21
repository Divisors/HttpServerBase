package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class HttpResponseByteBufferPayload implements HttpResponsePayload {
	protected ByteBuffer buffer;
	public HttpResponseByteBufferPayload(ByteBuffer buffer) {
		if (buffer == null)
			throw new NullPointerException();
		this.buffer = buffer;
	}
	@Override
	public long remaining() {
		return buffer.remaining();
	}

	@Override
	public void drainTo(Consumer<ByteBuffer> writer) {
		if (buffer == null)
			throw new IllegalStateException("Payload is closed!");
		writer.accept(buffer);
	}

	@Override
	public boolean isDone() {
		return remaining() == 0;
	}

	@Override
	public void close() {
		buffer = null;
	}

}

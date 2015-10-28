package com.mindlin.http;

import java.io.IOException;
import java.util.function.BiConsumer;

public interface WebSocket extends AutoCloseable {
	void onMessage(BiConsumer<WebSocket, byte[]> handler);
	void send(byte[] message) throws IOException;
	@Override
	void close();
}

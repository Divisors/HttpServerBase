package com.divisors.projectcuttlefish.httpserver.api.websocket;

import java.io.IOException;
import java.util.function.BiConsumer;

public interface WebSocket extends AutoCloseable{
	void send(String message) throws IOException;
	void onMessage(BiConsumer<WebSocket, String> handler);
	boolean isOpen();
	@Override
	void close() throws IOException;
	void close(long code) throws IOException;
	void close(String reason) throws IOException;
	void close(long code, String reason) throws IOException;
}

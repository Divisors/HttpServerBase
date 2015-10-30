package com.divisors.projectcuttlefish.httpserver.api.websocket;

import java.util.function.BiConsumer;

public interface WebSocket {
	void send(String message);
	void onMessage(BiConsumer<WebSocket, String> handler);
}

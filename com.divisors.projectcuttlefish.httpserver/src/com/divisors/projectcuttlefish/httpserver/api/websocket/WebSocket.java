package com.divisors.projectcuttlefish.httpserver.api.websocket;

import java.util.function.BiConsumer;

import org.eclipse.jetty.websocket.api.Session;

public interface WebSocket {
	void send(String message);
	void onMessage(BiConsumer<Session, String> handler);
}

package com.divisors.projectcuttlefish.httpserver.impl;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.api.websocket.WebSocket;

public class WebSocketImpl implements WebSocket {
	/**
	 * List of handlers that will be called when this recieves a message from the client.
	 */
	protected ArrayList<BiConsumer<WebSocket, String>> handlers;
	
	
	@Override
	public void send(String message) {
		
	}
	
	@Override
	public void onMessage(BiConsumer<WebSocket, String> handler) {
		(handlers==null?handlers=new ArrayList<>():handlers).add(handler);
	}
	
	public void onText(WebSocket session, String message) {
		
	}

}

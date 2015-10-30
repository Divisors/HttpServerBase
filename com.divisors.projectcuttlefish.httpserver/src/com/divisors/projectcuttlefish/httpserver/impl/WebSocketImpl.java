package com.divisors.projectcuttlefish.httpserver.impl;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.eclipse.jetty.websocket.api.Session;

import com.divisors.projectcuttlefish.httpserver.api.websocket.WebSocket;

public class WebSocketImpl implements WebSocket {
	/**
	 * List of handlers that will be called when this recieves a message from the client.
	 */
	protected ArrayList<BiConsumer<Session, String>> handlers;
	
	
	@Override
	public void send(String message) {
		
	}
	
	@Override
	public void onMessage(BiConsumer<Session, String> handler) {
		(handlers==null?handlers=new ArrayList<>():handlers).add(handler);
	}
	
	public void onText(Session session, String message) {
		
	}

}

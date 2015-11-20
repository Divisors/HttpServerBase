package com.divisors.projectcuttlefish.httpserver.impl.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;
import com.divisors.projectcuttlefish.httpserver.api.websocket.WebSocket;

public class WebSocketImpl implements WebSocket {
	/**
	 * List of handlers that will be called when this recieves a message from the client.
	 */
	protected ArrayList<BiConsumer<WebSocket, String>> handlers;
	protected final TcpConnection connection;
	
	public WebSocketImpl(TcpConnection c) {
		this.connection = c;
	}
	
	@Override
	public void send(String message) {
		
	}
	protected void open() {
		
	}
	@Override
	public void onMessage(BiConsumer<WebSocket, String> handler) {
		(handlers==null?handlers=new ArrayList<>():handlers).add(handler);
	}
	
	public void onText(WebSocket session, String message) {
		
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(long code) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(String reason) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close(long code, String reason) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

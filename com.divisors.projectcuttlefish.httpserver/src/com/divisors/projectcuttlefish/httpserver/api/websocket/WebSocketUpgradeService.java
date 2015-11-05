package com.divisors.projectcuttlefish.httpserver.api.websocket;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocket Servlet", urlPatterns = { "/ws" })
public class WebSocketUpgradeService {

	public void configure(Object factory) {
	}
	
}

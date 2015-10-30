package com.divisors.projectcuttlefish.httpserver.api.websocket;

import javax.servlet.annotation.WebServlet;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

@WebServlet(name = "WebSocket Servlet", urlPatterns = { "/ws" })
public class WebSocketUpgradeService extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(WebSocketServletFactory.class);
	}
	
}

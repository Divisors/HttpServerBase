package com.divisors.projectcuttlefish.httpserver.api;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Optional;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

public interface HttpExchange {
	HttpRequest getRequest();
	HttpResponse getResponse();
	InetAddress getRemoteAddress();
	int getPort();
	HttpServer getHttpServer();
	Optional<Socket> getSocket();
	boolean isOpen();
	HttpContext getContext();
}

package com.divisors.projectcuttlefish.httpserver.api;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

/**
 * Server for responding to HTTP requests. Usually upgrades one or more
 * {@link com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer TcpServer}'s.
 * @author mailmindlin
 * @see HttpServerFactory
 */
public interface HttpServer extends RunnableService, Server<HttpRequest, HttpResponse, HttpChannel> {
	//TODO figure out any methods to add
}

package com.divisors.projectcuttlefish.httpserver.api;

import java.util.Optional;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;

public interface HttpChannel extends Channel<HttpRequest, HttpResponse> {
	/**
	 * Get the server from which this channel was issued
	 * @return server
	 */
	HttpServer getHttpServer();
	/**
	 * Get the underlying tcpchannel, if any
	 * @return channel
	 */
	Optional<TcpChannel> getTcp();
	/**
	 * Get information about this channel's session
	 * @return context
	 */
	HttpContext getContext();
}

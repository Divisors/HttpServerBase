package com.divisors.projectcuttlefish.httpserver.api.http;

import java.util.Optional;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;

/**
 * A channel where HTTP requests and responses can be transfered. Note that sometimes
 * more than 1 {@link HttpRequest} may be sent over this channel (in the case of a HTTP persistant connection
 * or a HTTP/2 connection).
 * 
 * TODO: add a mechanism for preloading responses upon reading the parts of them
 * TODO: add a mechanism for error handlers
 * TODO: add a mechanism for pushing responses (if available)
 * @author mailmindlin
 */
public interface HttpChannel extends Channel<HttpRequest, HttpResponse> {
	/**
	 * Get the server from which this channel was issued
	 * @return server
	 */
	HttpServer getHttpServer();
	/**
	 * Get the underlying {@link com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel TcpChannel}, if any
	 * @return channel
	 */
	Optional<TcpChannel> getTcp();
	/**
	 * Get information about this channel's session
	 * @return context
	 */
	HttpContext getContext();
	/**
	 * Get connection id. Connection ids should be unique to each channel from any given server. SHOULD be the
	 * same ID as from the underlying TCP channel.
	 * @return connection id
	 */
	long getConnectionID();
}

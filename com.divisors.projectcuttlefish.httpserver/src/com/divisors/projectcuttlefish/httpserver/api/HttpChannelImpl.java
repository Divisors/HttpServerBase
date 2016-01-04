package com.divisors.projectcuttlefish.httpserver.api;

import java.util.Optional;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;

import reactor.fn.Consumer;

/**
 * Implementation of 
 * <br/>
 * Note that this is non-operational (it does literally nothing)
 * @author mailmindlin
 * @see HttpChannel
 */
public class HttpChannelImpl implements HttpChannel {

	@Override
	public Channel<HttpRequest, HttpResponse> write(HttpResponse data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel<HttpRequest, HttpResponse> onRead(Consumer<HttpRequest> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> Channel<X, Y> map(Codec<HttpRequest, HttpResponse, X, Y> codec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public HttpServer getHttpServer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<TcpChannel> getTcp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

}

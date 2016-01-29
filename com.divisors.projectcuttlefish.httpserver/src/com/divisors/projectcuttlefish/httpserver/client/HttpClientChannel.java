package com.divisors.projectcuttlefish.httpserver.client;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.ChannelOption;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

import reactor.fn.Consumer;

public class HttpClientChannel implements Channel<HttpResponse, HttpRequest> {
	@Override
	public Channel<HttpResponse, HttpRequest> write(HttpRequest data) {
		// TODO Auto-generated method stub
		return null;
		System.out.println("HTTPc:: writing request");
	}

	@Override
	public Channel<HttpResponse, HttpRequest> onRead(Consumer<HttpResponse> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <E> Channel<HttpResponse, HttpRequest> setOption(ChannelOption<E> key, E value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}

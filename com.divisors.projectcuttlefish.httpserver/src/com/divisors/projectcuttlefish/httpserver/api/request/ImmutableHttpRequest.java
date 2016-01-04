package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeaders;

/**
 * 
 * @author mailmindlin
 */
public final class ImmutableHttpRequest implements HttpRequest {
	protected final HttpHeaders headers;//TODO fix mutability
	protected final HttpRequestLine requestLine;
	public ImmutableHttpRequest(HttpRequest r) {
		headers = r.getHeaders();
		requestLine = r.getRequestLine().immutable();
	}
	@Override
	public boolean isMutable() {
		return false;
	}
	
	@Override
	public ImmutableHttpRequest immutable() {
		return this;
	}
	
	@Override
	public HttpRequestLine getRequestLine() {
		return this.requestLine;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
}

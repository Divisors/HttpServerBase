package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;

/**
 * An immutable HTTP request
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
	@Override
	public ImmutableHttpRequest addHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpRequest is immutable");
	}
	@Override
	public ImmutableHttpRequest addHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpRequest is immutable");
	}
	@Override
	public ImmutableHttpRequest setHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpRequest is immutable");
	}
	@Override
	public ImmutableHttpRequest setHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpRequest is immutable");
	}
	@Override
	public ImmutableHttpRequest removeHeader(String key) {
		throw new UnsupportedOperationException("ImmutableHttpRequest is immutable");
	}
}

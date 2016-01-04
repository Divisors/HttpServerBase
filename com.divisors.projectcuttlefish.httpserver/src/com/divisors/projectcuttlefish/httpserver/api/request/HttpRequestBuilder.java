package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.Builder;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaders;

/**
 * Builds HTTP requests
 * @author mailmindlin
 */
public class HttpRequestBuilder implements HttpRequest, Builder<HttpRequest> {
	protected HttpRequestLine requestLine;
	protected final HttpHeaders headers = new HttpHeaders();
	public HttpRequestBuilder setRequestLine(HttpRequestLine requestLine) {
		this.requestLine = requestLine;
		return this;
	}
	@Override
	public HttpRequest build() {
		return immutable();
	}
	@Override
	public boolean isMutable() {
		return true;
	}
	@Override
	public ImmutableHttpRequest immutable() {
		return new ImmutableHttpRequest(this);
	}
	@Override
	public HttpRequestLine getRequestLine() {
		return requestLine;
	}
	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}
	
	@Override
	public HttpHeader getHeader(String key) {
		return headers.getHeader(key);
	}
	
}
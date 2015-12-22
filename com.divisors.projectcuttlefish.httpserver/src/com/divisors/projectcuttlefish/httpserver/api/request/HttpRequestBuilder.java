package com.divisors.projectcuttlefish.httpserver.api.request;

import java.util.LinkedList;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.Builder;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;

/**
 * Builds HTTP requests
 * @author mailmindlin
 */
public class HttpRequestBuilder implements HttpRequest, Builder<HttpRequest> {
	protected final List<HttpHeader> headers = new LinkedList<>();
	HttpRequestLine requestLine;
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
	public List<HttpHeader> getHeaders() {
		return headers;
	}
	
}
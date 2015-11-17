package com.divisors.projectcuttlefish.httpserver.api.request;

import java.util.Collections;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;

public final class ImmutableHttpRequest implements HttpRequest {
	List<HttpHeader> headers;
	final HttpRequestLine requestLine;
	public ImmutableHttpRequest(HttpRequest r) {
		headers = Collections.unmodifiableList(r.getHeaders());
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
	public List<HttpHeader> getHeaders() {
		return headers;
	}

}

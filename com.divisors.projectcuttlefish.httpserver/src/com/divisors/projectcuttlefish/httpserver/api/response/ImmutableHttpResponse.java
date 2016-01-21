package com.divisors.projectcuttlefish.httpserver.api.response;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;

/**
 * Immutable HTTP response
 * @author mailmindlin
 * @see HttpResponse
 */
public class ImmutableHttpResponse implements HttpResponse {
	protected final HttpResponseLine line;
	protected final HttpHeaders headers;//TODO make immutable (version)
	/**
	 * Payload.
	 * <p>
	 * NOTE: this field is NOT immutable.
	 * </p>
	 */
	protected final HttpResponsePayload payload;
	public ImmutableHttpResponse(HttpResponseLine line, HttpHeaders headers, HttpResponsePayload body) {
		this.line = line.immutable();
		this.headers = headers;
		this.payload = body;
	}
	@Override
	public HttpResponseLine getResponseLine() {
		return line;
	}

	@Override
	public HttpHeaders getHeaders() {
		return headers;
	}

	@Override
	public ImmutableHttpResponse addHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public ImmutableHttpResponse addHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public ImmutableHttpResponse setHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public ImmutableHttpResponse setHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public ImmutableHttpResponse removeHeader(String key) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public HttpResponsePayload getBody() {
		return this.payload;
	}
	@Override
	public HttpHeader getHeader(String key) {
		return headers.getHeader(key);
	}
	@Override
	public boolean isMutable() {
		return false;
	}
	@Override
	public ImmutableHttpResponse immutable() {
		return this;
	}
	@Override
	public HttpResponse setBody(HttpResponsePayload payload) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}
}

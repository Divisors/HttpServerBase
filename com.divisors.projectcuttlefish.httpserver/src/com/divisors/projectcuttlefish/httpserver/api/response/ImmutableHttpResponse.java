package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.channels.SeekableByteChannel;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaders;
import com.divisors.projectcuttlefish.httpserver.api.SeekableInputStream;

/**
 * Immutable HTTP response
 * @author mailmindlin
 * @see HttpResponse
 */
public class ImmutableHttpResponse implements HttpResponse {
	protected final HttpResponseLine line;
	protected final HttpHeaders headers;//TODO make immutable (version)
	public ImmutableHttpResponse(HttpResponseLine line, HttpHeaders headers, SeekableInputStream body) {
		this.line = line.immutable();
		this.headers = headers;
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
	public boolean addHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public boolean addHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public boolean setHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public boolean setHeader(String key, String... values) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public void removeHeader(String key) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public SeekableByteChannel getResponseBody() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HttpHeader getHeader(String key) {
		return headers.getHeader(key);
	}
}

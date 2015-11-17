package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.channels.SeekableByteChannel;
import java.util.Collections;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaderValue;
import com.divisors.projectcuttlefish.httpserver.api.SeekableInputStream;

public class ImmutableHttpResponse implements HttpResponse {
	HttpResponseLine line;
	List<HttpHeader> headers;
	public ImmutableHttpResponse(HttpResponseLine line, List<HttpHeader> headers, SeekableInputStream body) {
		this.line = line.immutable();
		this.headers = Collections.unmodifiableList(headers);
	}
	@Override
	public HttpResponseLine getResponseLine() {
		return line;
	}

	@Override
	public List<HttpHeader> getHeaders() {
		return headers;
	}

	@Override
	public boolean addHeader(HttpHeader header) {
		throw new UnsupportedOperationException("ImmutableHttpResponse is immutable");
	}

	@Override
	public boolean addHeader(HttpHeaderValue header) {
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
	public boolean setHeader(HttpHeaderValue header) {
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

}

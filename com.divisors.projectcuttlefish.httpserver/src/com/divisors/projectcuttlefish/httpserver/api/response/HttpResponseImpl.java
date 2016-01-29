package com.divisors.projectcuttlefish.httpserver.api.response;

import java.util.Arrays;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;

public class HttpResponseImpl implements HttpResponse {
	protected HttpResponseLine responseLine;
	protected HttpHeaders headers;
	protected HttpResponsePayload body;
	public HttpResponseImpl() {
		this(new HttpResponseLineImpl(), new HttpHeaders());
	}
	public HttpResponseImpl(HttpResponseLine responseLine) {
		this(responseLine, new HttpHeaders());
	}
	public HttpResponseImpl(HttpResponseLine responseLine, HttpHeaders headers) {
		this.responseLine = responseLine;
		this.headers = headers;
	}
	@Override
	public HttpResponseLine getResponseLine() {
		return this.responseLine;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	@Override
	public HttpHeader getHeader(String key) {
		return headers.getHeader(key);
	}

	@Override
	public HttpResponseImpl addHeader(HttpHeader header) {
		headers.add(header);
		return this;
	}

	@Override
	public HttpResponseImpl addHeader(String key, String... values) {
		headers.addAll(key, Arrays.asList(values));
		return this;
	}

	@Override
	public HttpResponseImpl setHeader(HttpHeader header) {
		headers.put(header);
		return this;
	}

	@Override
	public HttpResponseImpl setHeader(String key, String... values) {
		headers.put(key, Arrays.asList(values));
		return this;
	}

	@Override
	public HttpResponseImpl removeHeader(String key) {
		headers.remove(key);
		return this;
	}
	@Override
	public boolean isMutable() {
		return true;
	}
	@Override
	public ImmutableHttpResponse immutable() {
		return null;
	}
	@Override
	public HttpResponseImpl setBody(HttpResponsePayload payload) {
		this.body = payload;
		return this;
	}
	@Override
	public HttpResponsePayload getBody() {
		return this.body;
	}
	
}

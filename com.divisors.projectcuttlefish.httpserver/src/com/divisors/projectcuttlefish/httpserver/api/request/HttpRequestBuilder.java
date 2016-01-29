package com.divisors.projectcuttlefish.httpserver.api.request;

import java.util.Arrays;

import com.divisors.projectcuttlefish.httpserver.api.Builder;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;

/**
 * Builds HTTP requests
 * @author mailmindlin
 */
public class HttpRequestBuilder implements HttpRequest, Builder<HttpRequest> {
	protected HttpRequestLineBuilder requestLine = new HttpRequestLineBuilder();
	protected final HttpHeaders headers = new HttpHeaders();
	public HttpRequestBuilder setMethod(String method) {
		requestLine.setMethod(method);
		return this;
	}
	public HttpRequestBuilder setPath(String path) {
		requestLine.setPath(path);
		return this;
	}
	public HttpRequestBuilder setHttpVersion(String version) {
		requestLine.setVersion(version);
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
	@Override
	public HttpRequestBuilder addHeader(HttpHeader header) {
		getHeaders().add(header);
		return this;
	}
	@Override
	public HttpRequestBuilder addHeader(String key, String... values) {
		getHeaders().addAll(key, Arrays.asList(values));
		return this;
	}
	@Override
	public HttpRequestBuilder setHeader(HttpHeader header) {
		getHeaders().put(header);
		return this;
	}
	@Override
	public HttpRequestBuilder setHeader(String key, String... values) {
		getHeaders().put(key, values);
		return this;
	}
	@Override
	public HttpRequestBuilder removeHeader(String key) {
		getHeaders().remove(key);
		return this;
	}
	
}
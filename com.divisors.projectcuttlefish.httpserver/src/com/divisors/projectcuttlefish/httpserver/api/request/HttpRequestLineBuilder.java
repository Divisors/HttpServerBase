package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.Builder;

/**
 * Builder for HTTP request lines. Doubles as a generally mutable HttpRequestLine.
 * @author mailmindlin
 */
public class HttpRequestLineBuilder implements HttpRequestLine, Builder<ImmutableHttpRequestLine> {
	/**
	 * HTTP method
	 */
	protected String method;
	/**
	 * HTTP path
	 */
	protected String path;
	/**
	 * HTTP version. Defaults to "HTTP/1.1".
	 */
	protected String version = HttpRequest.HTTP_1_1;
	public HttpRequestLineBuilder setMethod(String method) {
		this.method = method;
		return this;
	}
	
	public HttpRequestLineBuilder setPath(String path) {
		this.path = path;
		return this;
	}

	public HttpRequestLineBuilder setVersion(String version) {
		this.version = version;
		return this;
	}
	
	@Override
	public ImmutableHttpRequestLine build() {
		return new ImmutableHttpRequestLine(this.method, this.path, this.version);
	}

	@Override
	public HttpRequestLine immutable() {
		return build();
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getHttpVersion() {
		return version;
	}

	@Override
	public boolean isMutable() {
		return true;
	}
	
}

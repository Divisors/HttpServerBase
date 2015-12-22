package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.Builder;

public class HttpRequestLineBuilder implements Builder<ImmutableHttpRequestLine> {
	protected String method, path, version;
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
	
}

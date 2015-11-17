package com.divisors.projectcuttlefish.httpserver.api.request;

public final class ImmutableHttpRequestLine implements HttpRequestLine {
	protected final String method;
	protected final String path;
	protected final String httpv;
	public ImmutableHttpRequestLine (String method, String path, String httpv) {
		this.method = method;
		this.path = path;
		this.httpv = httpv;
	}
	
	public ImmutableHttpRequestLine (HttpRequestLine source) {
		this(source.getMethod(), source.getPath(), source.getHttpVersion());
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
		return httpv;
	}

	@Override
	public ImmutableHttpRequestLine immutable() {
		return this;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}

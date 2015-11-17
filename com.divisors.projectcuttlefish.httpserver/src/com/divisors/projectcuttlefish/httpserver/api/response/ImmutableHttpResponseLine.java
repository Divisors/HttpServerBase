package com.divisors.projectcuttlefish.httpserver.api.response;

public final class ImmutableHttpResponseLine implements HttpResponseLine {
	protected final String httpv;
	protected final int code;
	protected final String text;
	public ImmutableHttpResponseLine(String httpv, int code, String text) {
		this.httpv = httpv;
		this.code = code;
		this.text = text;
	}
	public ImmutableHttpResponseLine(HttpResponseLine other) {
		this(other.getHttpVersion(), other.getStatusCode(), other.getStatusText());
	}
	@Override
	public HttpResponseLine immutable() {
		return this;
	}

	@Override
	public String getHttpVersion() {
		return httpv;
	}

	@Override
	public int getStatusCode() {
		return code;
	}

	@Override
	public String getStatusText() {
		return text;
	}

	@Override
	public boolean isMutable() {
		return false;
	}
	@Override
	public String toString() {
		return this.getText();
	}
}

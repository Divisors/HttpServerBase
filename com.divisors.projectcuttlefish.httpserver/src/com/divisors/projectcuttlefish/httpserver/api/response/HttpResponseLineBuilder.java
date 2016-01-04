package com.divisors.projectcuttlefish.httpserver.api.response;

import com.divisors.projectcuttlefish.httpserver.api.Builder;

public class HttpResponseLineBuilder implements Builder<ImmutableHttpResponseLine>{
	protected String httpv, message;
	protected int code;
	public HttpResponseLineBuilder() {
		
	}
	public HttpResponseLineBuilder(String httpv, int code, String message) {
		this.httpv = httpv;
		this.code = code;
		this.message = message;
	}
	public HttpResponseLineBuilder setHttpv(String value) {
		httpv = value;
		return this;
	}
	public HttpResponseLineBuilder setCode(int code) {
		this.code = code;
		return this;
	}
	public HttpResponseLineBuilder setMessage(String message) {
		this.message = message;
		return this;
	}
	public HttpResponseLineBuilder guessMessage() {
		//TODO finish
		throw new UnsupportedOperationException("Not yet implemented...");
	}
	@Override
	public ImmutableHttpResponseLine build() {
		return new ImmutableHttpResponseLine(httpv,code,message);
	}

}

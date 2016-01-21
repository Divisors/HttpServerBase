package com.divisors.projectcuttlefish.httpserver.api.response;

public class HttpResponseLineImpl implements HttpResponseLine {
	protected String httpv, message;
	protected int code;
	public HttpResponseLineImpl() {
		
	}
	public HttpResponseLineImpl(String httpv, int code, String message) {
		this.httpv = httpv;
		this.code = code;
		this.message = message;
	}
	public HttpResponseLineImpl setHttpv(String value) {
		httpv = value;
		return this;
	}
	public HttpResponseLineImpl setCode(int code) {
		this.code = code;
		return this;
	}
	public HttpResponseLineImpl setStatusText(String message) {
		this.message = message;
		return this;
	}
	/**
	 * Attempts to guess status text from code
	 * @return
	 */
	public HttpResponseLineImpl guessMessage() {
		//TODO finish
		throw new UnsupportedOperationException("Not yet implemented...");
	}
	@Override
	public HttpResponseLine immutable() {
		return new ImmutableHttpResponseLine(this);
	}
	@Override
	public String getHttpVersion() {
		return this.httpv;
	}
	@Override
	public int getStatusCode() {
		return this.code;
	}
	@Override
	public String getStatusText() {
		return this.message;
	}
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@Override
	public String toString() {
		return httpv + ' ' + code + ' ' + message;
	}
}

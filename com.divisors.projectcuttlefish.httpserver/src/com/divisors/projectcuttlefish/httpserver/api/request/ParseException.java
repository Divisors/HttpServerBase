package com.divisors.projectcuttlefish.httpserver.api.request;

public class ParseException extends RuntimeException {
	private static final long serialVersionUID = 6852954719652680474L;
	public ParseException() {
		super();
	}
	public ParseException(String message) {
		super(message);
	}
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
	public ParseException(Throwable cause) {
		super(cause);
	}
}

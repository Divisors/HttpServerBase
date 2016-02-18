package com.divisors.projectcuttlefish.crypto.api.jwt;

public class JWTParsingException extends Exception {
	private static final long serialVersionUID = -3663431489684693386L;

	public JWTParsingException() {
		super();
	}
	
	public JWTParsingException(String problem) {
		super(problem);
	}
	
	public JWTParsingException(Throwable cause) {
		super(cause);
	}
	
	public JWTParsingException(String problem, Throwable cause) {
		super(problem, cause);
	}
}

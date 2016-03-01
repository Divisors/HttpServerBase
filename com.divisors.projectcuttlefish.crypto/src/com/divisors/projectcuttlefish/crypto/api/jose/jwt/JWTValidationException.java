package com.divisors.projectcuttlefish.crypto.api.jose.jwt;

/**
 * Thrown if a JWT's integrity cannot be validated
 * @author mailmindlin
 */
public class JWTValidationException extends Exception {
	private static final long serialVersionUID = -5245556959439207408L;

	public JWTValidationException() {
		super();
	}
	
	public JWTValidationException(String problem) {
		super(problem);
	}
	
	public JWTValidationException(Throwable cause) {
		super(cause);
	}
	
	public JWTValidationException(String problem, Throwable cause) {
		super(problem, cause);
	}
}

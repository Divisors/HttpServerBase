package com.divisors.projectcuttlefish.uac.api;

import java.security.AccessControlException;

/**
 * Thrown when a session is invalid
 * @author mailmindlin
 *
 */
public class InvalidSessionException extends AccessControlException {
	private static final long serialVersionUID = 860133522342337551L;
	
	public InvalidSessionException(String problem) {
		super(problem);
	}

}

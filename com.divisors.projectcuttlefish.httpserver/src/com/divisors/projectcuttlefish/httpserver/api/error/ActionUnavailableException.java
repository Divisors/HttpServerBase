package com.divisors.projectcuttlefish.httpserver.api.error;

/**
 * Thrown when {@link com.divisors.projectcuttlefish.httpserver.api.Action#act Action.act()}
 * is called on an action, when it is unavailable.
 * @author mailmindlin
 * @see com.divisors.projectcuttlefish.httpserver.api.Action Action
 */
public class ActionUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 4597365109610703175L;
	public ActionUnavailableException() {
		super();
	}
	public ActionUnavailableException(String string) {
		super(string);
	}
}

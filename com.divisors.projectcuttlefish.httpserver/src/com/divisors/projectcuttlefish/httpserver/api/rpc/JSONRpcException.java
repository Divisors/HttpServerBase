package com.divisors.projectcuttlefish.httpserver.api.rpc;

import org.json.JSONObject;

public class JSONRpcException extends Exception {
	private static final long serialVersionUID = -6456893475298890612L;
	
	protected final JSONObject json;
	
	public JSONRpcException(JSONObject json) {
		super(json.getString("message"));
		this.json = json;
	}
	public JSONObject getJSON() {
		return this.json;
	}
	/**
	 * Get error code, which identifies the error type
	 * @return error code
	 */
	public int getCode() {
		return getJSON().getInt("code");
	}
	/**
	 * Get a human-readable error message in English suitable for printing in a log file
	 * or as part of an error message to be displayed to a user.
	 * @return message
	 */
	public String getMessage() {
		return getJSON().getString("message");
	}
	/**
	 * Any values that the client needs to construct its own error message, for example
	 * in a different language than English.
	 * @return data
	 */
	public Object getData() {
		return getJSON().opt("data");
	}
}

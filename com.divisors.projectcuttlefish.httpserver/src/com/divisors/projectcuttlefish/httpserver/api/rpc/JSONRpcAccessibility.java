package com.divisors.projectcuttlefish.httpserver.api.rpc;

public enum JSONRpcAccessibility {
	/**
	 * This method is not accessible to RPC clients. Default.
	 */
	PRIVATE,
	/**
	 * This method is only invokable over secured (e.g., HTTPS) connections
	 */
	SECURED,
	/**
	 * This method is invokable over any connection.
	 */
	PUBLIC
}

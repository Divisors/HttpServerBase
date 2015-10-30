package com.divisors.projectcuttlefish.httpserver.api;

public interface HttpContext {
	default boolean isSSL() {
		return false;
	}
}

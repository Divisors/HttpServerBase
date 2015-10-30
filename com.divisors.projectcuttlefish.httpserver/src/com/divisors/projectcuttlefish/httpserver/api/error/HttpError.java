package com.divisors.projectcuttlefish.httpserver.api.error;

public interface HttpError {
	int getErrorCode();
	default String getErrorMessage() {
		return null;
	}
}

package com.divisors.projectcuttlefish.httpserver.client;

public enum HttpClientReadyState {
	UNSENT,
	OPENED,
	HEADERS_RECIEVED,
	LOADING,
	DONE;
}

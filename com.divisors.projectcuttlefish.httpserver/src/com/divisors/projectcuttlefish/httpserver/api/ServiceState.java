package com.divisors.projectcuttlefish.httpserver.api;

public enum ServiceState {
	UNINITIALIZED,
	INITIALIZED,
	STARTING,
	RUNNING,
	STOPPING,
	DESTROYED,
	UNKNOWN,
	OTHER;
}

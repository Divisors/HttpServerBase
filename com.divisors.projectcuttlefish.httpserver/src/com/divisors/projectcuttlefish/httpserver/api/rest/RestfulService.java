package com.divisors.projectcuttlefish.httpserver.api.rest;

import java.util.concurrent.Future;

public interface RestfulService<T> {
	RestfulApi getApi();
	String getRelativePath();
	
	default String getPath() {
		return getApi().getPath() + getRelativePath();
	}
	
	String getMethod();
	
	boolean canDoAction(String method);
	Future<T> doAction(String method, Object...data) throws IllegalArgumentException;
}

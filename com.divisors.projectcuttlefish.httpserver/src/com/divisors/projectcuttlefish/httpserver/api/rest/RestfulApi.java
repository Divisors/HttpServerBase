package com.divisors.projectcuttlefish.httpserver.api.rest;

import java.util.Enumeration;

public abstract class RestfulApi {
	protected final String baseURL;
	protected RestfulApi (String baseURL) {
		this.baseURL = baseURL;
	}
	
	public abstract String getPath();
	
	public abstract RestfulService<?> getService(String relativePath) throws IllegalArgumentException;
	
	public abstract Enumeration<String> getServices();
}

package com.divisors.projectcuttlefish.httpserver.api;

import java.util.Map.Entry;

public class HttpHeaderValue implements Entry<String, String>{
	protected final String key, value;

	public HttpHeaderValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		throw new UnsupportedOperationException("HttpHeaderValue is immutable");
	}
	
}

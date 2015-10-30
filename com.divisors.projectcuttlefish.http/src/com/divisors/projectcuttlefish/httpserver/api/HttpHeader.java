package com.divisors.projectcuttlefish.httpserver.api;

import java.util.Map;
import java.util.Optional;

public class HttpHeader implements Map.Entry<String, String[]> {
	protected final String key;
	protected final String[] values;
	public HttpHeader (String key, String...values) {
		this.key = key;
		this.values = values;
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String[] getValue() {
		return values;
	}

	@Override
	public String[] setValue(String[] arg0) {
		throw new UnsupportedOperationException("HttpHeader is immutable");
	}
	
	public Optional<HttpHeaderValue> first() {
		return values.length>0 ? Optional.of(new HttpHeaderValue(key, values[0])) : Optional.empty();
	}
	
	public HttpHeaderValue[] toArray() {
		HttpHeaderValue[] result = new HttpHeaderValue[values.length];
		for (int i=0;i<values.length;i++)
			result[i] = new HttpHeaderValue(key, values[i]);
		return result;
	}
}

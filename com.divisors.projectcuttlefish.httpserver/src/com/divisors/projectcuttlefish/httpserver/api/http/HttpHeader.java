package com.divisors.projectcuttlefish.httpserver.api.http;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Explains a one-to-many relationship between HTTP header keys and values.
 * <br/>
 * Most headers will have one value, but RFC 2616 says that multiple headers with the same 'field-name' can
 * be used, as long as they can be concatenated by adding commas.
 * 
 * (thanks to http://stackoverflow.com/a/4371395/2759984)
 * @author mailmindlin
 */
public class HttpHeader implements Map.Entry<String, Collection<String>> {
	protected final String key;
	protected final Collection<String> values;
	public HttpHeader(String key, Collection<String> values) {
		this.key = key;
		if (values == null)
			throw new IllegalArgumentException("Values cannot be null");
		this.values = values;
	}
	public HttpHeader(String key, String...values) {
		this.key = key;
		this.values = Arrays.asList(values);
	}
	
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public Collection<String> getValue() {
		return values;
	}
	public String first() {
		if (values.isEmpty())
			return null;
		return values.iterator().next();
	}
	public String flatValue() {
		StringBuilder result = new StringBuilder();
		for (String value : values)
			result.append(value).append(", ");
		result.delete(result.length()-2,result.length());
		return result.toString();
	}
	
	@Override
	public Collection<String> setValue(Collection<String> arg0) {
		throw new UnsupportedOperationException("HttpHeader is immutable");
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(this.getKey()).append(": ");
		if (values.size() == 0)
			throw new IllegalStateException("HTTP header {key:"+getKey()+"} has 0 values!");
		for (String value : values)
			result.append(value).append(", ");
		result.delete(result.length()-2, result.length());//remove last 2 characters from end
		return result.toString();
	}
}

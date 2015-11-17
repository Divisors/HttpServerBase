package com.divisors.projectcuttlefish.httpserver.api.request;

import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.Mutable;

/**
 * Wrapper for HTTP request
 * 
 * @author mailmindlin
 *
 */
public interface HttpRequest extends Mutable<HttpRequest> {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_TRACE = "TRACE";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_CONNECT = "CONNECT";
	public static final String HTTP_1 = "HTTP/1.0";
	public static final String HTTP_1_1 = "HTTP/1.1";
	public static final String HTTP_2 = "HTTP/2";

	default String getMethod() {
		return getRequestLine().getMethod();
	}

	default String getPath() {
		return getRequestLine().getPath();
	}

	default String getHttpVersion() {
		return getRequestLine().getHttpVersion();
	}

	HttpRequestLine getRequestLine();
	
	List<HttpHeader> getHeaders();
	default HttpHeader getHeader(String key) {
		for (HttpHeader header : getHeaders())
			if (header.getKey().equalsIgnoreCase(key))
				return header;
		return null;
	}
	
	default ImmutableHttpRequest immutable() {
		return new ImmutableHttpRequest(this);
	}
}

package com.divisors.projectcuttlefish.httpserver.api.request;

import com.divisors.projectcuttlefish.httpserver.api.Mutable;

public interface HttpRequestLine extends Mutable<HttpRequestLine> {
	
	String getMethod();
	String getPath();
	String getHttpVersion();
	
	default HttpRequestLine immutable() {
		return new ImmutableHttpRequestLine(this);
	}
	
	boolean isMutable();
}

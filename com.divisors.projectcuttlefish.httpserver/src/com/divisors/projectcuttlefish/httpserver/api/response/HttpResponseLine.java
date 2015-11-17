package com.divisors.projectcuttlefish.httpserver.api.response;

import com.divisors.projectcuttlefish.httpserver.api.Mutable;

public interface HttpResponseLine extends Mutable<HttpResponseLine> {
	String getHttpVersion();
	int getStatusCode();
	String getStatusText();
	default String getText() {
		return getHttpVersion() + ' ' + getStatusCode() + ' ' + getStatusText();
	}
	@Override
	boolean isMutable();
	@Override
	default HttpResponseLine immutable() {
		return new ImmutableHttpResponseLine(this);
	}
}

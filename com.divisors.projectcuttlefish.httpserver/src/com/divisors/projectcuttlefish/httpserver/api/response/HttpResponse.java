package com.divisors.projectcuttlefish.httpserver.api.response;

import com.divisors.projectcuttlefish.httpserver.api.Mutable;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;

/**
 * Represents a HTTP response
 * @author mailmindlin
 * @see HttpResponseLine
 * @see ImmutableHttpResponse
 */
public interface HttpResponse extends Mutable<ImmutableHttpResponse> {
	HttpResponseLine getResponseLine();
	
	HttpHeaders getHeaders();
	HttpHeader getHeader(String key);
	HttpResponse addHeader(HttpHeader header);
	HttpResponse addHeader(String key, String...values);
	HttpResponse setHeader(HttpHeader header);
	HttpResponse setHeader(String key, String...values);
	HttpResponse removeHeader(String key);
	
	HttpResponse setBody(HttpResponsePayload payload);
	HttpResponsePayload getBody();
	
	@Override
	default boolean isMutable() {
		return true;
	}
	@Override
	default ImmutableHttpResponse immutable() {
		return new ImmutableHttpResponse(getResponseLine(), getHeaders(), getBody());
	}
}

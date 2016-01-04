package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.channels.SeekableByteChannel;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaders;

/**
 * Represents a HTTP response
 * @author mailmindlin
 * @see HttpResponseLine
 * @see ImmutableHttpResponse
 */
public interface HttpResponse {
	HttpResponseLine getResponseLine();
	
	HttpHeaders getHeaders();
	HttpHeader getHeader(String key);
	boolean addHeader(HttpHeader header);
	boolean addHeader(String key, String...values);
	boolean setHeader(HttpHeader header);
	boolean setHeader(String key, String...values);
	void removeHeader(String key);
	
	SeekableByteChannel getResponseBody();
}

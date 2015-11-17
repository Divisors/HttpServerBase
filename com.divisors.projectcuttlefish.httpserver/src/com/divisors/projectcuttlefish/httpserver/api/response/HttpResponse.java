package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.channels.SeekableByteChannel;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaderValue;

public interface HttpResponse {
	HttpResponseLine getResponseLine();
	
	List<HttpHeader> getHeaders();
	default HttpHeader getHeader(String key) {
		for(HttpHeader header : getHeaders())
			if (header.getKey().equalsIgnoreCase(key))
				return header;
		return null;
	}
	boolean addHeader(HttpHeader header);
	boolean addHeader(HttpHeaderValue header);
	boolean addHeader(String key, String...values);
	boolean setHeader(HttpHeader header);
	boolean setHeader(HttpHeaderValue header);
	boolean setHeader(String key, String...values);
	void removeHeader(String key);
	
	SeekableByteChannel getResponseBody();
}

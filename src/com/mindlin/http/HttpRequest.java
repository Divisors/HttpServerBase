package com.mindlin.http;

import java.net.Socket;
import java.util.Optional;

public interface HttpRequest {
	RequestLine getRequestLine();
	HttpHeader[] getHeaders();
	default HttpHeader getHeader(String key) {
		for (HttpHeader header : getHeaders())
			if(header.getName().equalsIgnoreCase(key))
				return header;
		return null;
	}
	Optional<Socket> getSocket();
}

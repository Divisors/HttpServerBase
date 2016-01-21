package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.ByteBuffer;

public class StandardHttpResponseSerializer {
	public static ByteBuffer serialize(HttpResponse response) {
		StringBuilder headers = new StringBuilder(response.getResponseLine().toString())
				.append("\r\n")
				.append(response.getHeaders().toString())
				.append("\r\n");
		byte[] bytes = headers.toString().getBytes();
		ByteBuffer b = ByteBuffer.allocate(bytes.length + (int)response.getBody().remaining());//TODO: fix for big things
		b.put(bytes);
		response.getBody().drainTo(b::put);
		b.flip();
		return b;
	}
}

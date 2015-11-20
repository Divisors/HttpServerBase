package com.divisors.projectcuttlefish.httpserver.impl;

import java.nio.ByteBuffer;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;

public class HttpRequestParser {
	boolean firstLine = true;
	byte[] previous = new byte[0];
	public HttpRequestParser() {
		
	}
	/**
	 * Finds first index of CRLF (0x0D0A) in buffer starting at start, and continuing to len
	 * @param b
	 * @param start
	 * @param len
	 * @return
	 */
	int findCRLF(ByteBuffer b, int start, int len) {
		for (int i=start; i < start + len; i++)
			//TODO finish
				return i;
		return -1;
	}
	boolean parse(byte[] bytes) {
		ByteBuffer buf = ByteBuffer.allocate(previous.length + bytes.length)
				.put(previous)
				.put(bytes);
		buf.rewind();
		
		return false;
	}
	HttpRequest getRequest() {
		return null;//TODO finish
	}
}

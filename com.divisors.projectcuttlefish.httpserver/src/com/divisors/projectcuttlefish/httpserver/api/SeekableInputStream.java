package com.divisors.projectcuttlefish.httpserver.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public abstract class SeekableInputStream extends InputStream implements SeekableByteChannel {

	@Override
	public long size() throws IOException {
		return available();
	}

	@Override
	public int write(ByteBuffer arg0) throws IOException {
		throw new UnsupportedOperationException();
	}
}

package com.divisors.projectcuttlefish.httpserver.api.compression;

public class HttpGzipCompressor implements HttpCompressor {

	@Override
	public boolean test(String[] algorithms) {
		for (String algorithm : algorithms)
			if (algorithm.equalsIgnoreCase("GZIP"))
				return true;
		return false;
	}

	@Override
	public byte[] compress(byte[] data, int off, int len) {
		// TODO Auto-generated method stub
		return null;
	}

}

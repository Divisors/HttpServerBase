package com.divisors.projectcuttlefish.httpserver.api.compression;

import java.util.function.Predicate;

/**
 * 
 * @author mailmindlin
 *
 */
public interface HttpCompressor extends Predicate<String[]>{
	/**
	 * Test whether this compressor is valid for any of the given compression methods
	 */
	@Override
	public boolean test(String[] algorithms);
}

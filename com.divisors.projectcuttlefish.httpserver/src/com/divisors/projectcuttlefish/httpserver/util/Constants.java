package com.divisors.projectcuttlefish.httpserver.util;

import java.util.function.Function;

/**
 * Some nice constants.
 * @author mailmindlin
 */
public class Constants {
	/**
	 * Constant HTTP newline bytes, for hardcoding.
	 * @see #HTTP_NEWLINE_CHARS
	 */
	public static final byte[] HTTP_NEWLINE = new byte[]{'\r','\n'};
	/**
	 * Constant HTTP newline characters, for hardcoding.
	 * @see #HTTP_NEWLINE
	 */
	public static final char[] HTTP_NEWLINE_CHARS = new char[]{'\r','\n'};
	/**
	 * An identity function. Any input is returned, unmodified.
	 */
	public static final Function<?, ?> IDENTITY_FN = (x) -> (x);
}

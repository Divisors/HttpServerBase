package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.regex.Pattern;

/**
 * Series of operating systems.
 * 
 * For example, Windows Vista, Windows 8, and Windows 10 would all be under the family 'Windows'
 * @author mailmindling
 * @see UAOperatingSystem
 */
public enum UAOSFamily {
	ANDROID("Android"),
	CHROME_OS("Chrome OS"),
	IOS("iOS","iOS|iPhone OS"),
	LINUX("Linux"),
	MAC_OS("Mac OS","(Mac OS X|OS X)"),
	NINTENDO("Nintendo"),
	XBOX("Xbox"),
	WINDOWS("Windows"),
	UNKNOWN("?","^$"),
	;
	protected final String name;
	protected final Pattern pattern;
	private UAOSFamily(String name) {
		this(name, name);
	}
	private UAOSFamily(String name, String pattern) {
		this.name = name;
		this.pattern = Pattern.compile(pattern);
	}
}

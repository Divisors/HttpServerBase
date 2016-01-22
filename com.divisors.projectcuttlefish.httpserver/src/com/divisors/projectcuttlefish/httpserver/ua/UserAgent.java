package com.divisors.projectcuttlefish.httpserver.ua;

/**
 * A single user agent
 * @author mailmindlin
 */
public interface UserAgent {
	UAFamily getFamily();
	UADeviceCategory getDeviceCategory();
	UAOperatingSystem getOperatingSystem();
	/**
	 * Get UA string
	 * @return UA string
	 */
	String getString();
	UASecurity getSecurity();
}

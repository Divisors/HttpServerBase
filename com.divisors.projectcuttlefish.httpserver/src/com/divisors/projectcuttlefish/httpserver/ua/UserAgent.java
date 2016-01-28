package com.divisors.projectcuttlefish.httpserver.ua;

/**
 * A single user agent
 * @author mailmindlin
 */
public interface UserAgent {
	UABrowser getBrowser();
	UADeviceCategory getDeviceCategory();
	UAOperatingSystem getOperatingSystem();
	/**
	 * Get UA string
	 * @return UA string
	 */
	String getString();
	UASecurity getSecurity();
}

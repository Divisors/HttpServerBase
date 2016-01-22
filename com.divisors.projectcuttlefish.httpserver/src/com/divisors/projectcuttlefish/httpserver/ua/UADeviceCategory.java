package com.divisors.projectcuttlefish.httpserver.ua;

public enum UADeviceCategory {
	/**
	 * 
	 */
	DESKTOP,
	EMBEDED,
	GAME_CONSOLE,
	/**
	 * A phone that has internet capabilities. Most likely less powerful CPU, and a touch screen.
	 * Probably no keyboard. Possible slow or metered connection speeds.
	 */
	SMARTPHONE,
	/**
	 * A larger wireless device. Most likely has a touch screen, 
	 */
	TABLET,
	/**
	 * 
	 */
	WEARABLE,
	/**
	 * Crawler robot, like GoogleBot.
	 */
	ROBOT,
	/**
	 * A client that is only looking to use a text-based API. Could be embedded or something, like a RPi.
	 */
	TEXT_API,
	/**
	 * A really slow client with a large display. Includes smart tvs, and streaming devices like
	 * Chromecasts or Amazon Fire Sticks.
	 */
	TV,
	/**
	 * We know what it is, but it's not one of these.
	 */
	OTHER,
	/**
	 * Absolutely no idea what it is. Be careful.
	 */
	UNKNOWN;
	protected final String name;
	private UADeviceCategory() {
		this.name = this.name().toLowerCase().replace('_', ' ');
	}
	public String prettyName() {
		return name;
	}
}

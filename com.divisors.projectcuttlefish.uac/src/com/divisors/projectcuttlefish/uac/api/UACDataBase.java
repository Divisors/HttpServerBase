package com.divisors.projectcuttlefish.uac.api;

public abstract class UACDataBase {
	protected final String host, username;
	private final char[] password;
	public UACDataBase(String host) {
		this(host, null, null);
	}
	public UACDataBase(String host, String username) {
		this(host, username, null);
	}
	public UACDataBase(String host, String username, char[] password) {
		this.host = host;
		this.username = username;
		this.password = password;
	}
}

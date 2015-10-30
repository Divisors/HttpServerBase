package com.divisors.projectcuttlefish.uac;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	protected ConcurrentHashMap<Session, Void> sessions = new ConcurrentHashMap<>();
	
	
	
	public boolean isValid(Session session) {
		return false;//TODO finish;
	}
}

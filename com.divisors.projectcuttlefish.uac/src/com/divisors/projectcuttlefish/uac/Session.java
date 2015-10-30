package com.divisors.projectcuttlefish.uac;

import java.util.UUID;

public interface Session {
	UUID getID();
	boolean isValid();
	User getUser();
}

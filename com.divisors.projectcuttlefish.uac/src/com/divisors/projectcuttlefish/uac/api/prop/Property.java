package com.divisors.projectcuttlefish.uac.api;

public interface Property {
	PropertyReference getReference();
	boolean updatedSince(Instant timestamp);
}

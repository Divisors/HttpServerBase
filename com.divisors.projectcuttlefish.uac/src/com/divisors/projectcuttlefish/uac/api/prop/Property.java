package com.divisors.projectcuttlefish.uac.api.prop;

public interface Property {
	PropertyReference getReference();
	boolean modifiedSince(Instant timestamp);
}

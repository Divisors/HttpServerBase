package com.divisors.projectcuttlefish.crypto.api.jwt;

public interface JWTClaim {
	String getName();
	default String getPrettyName() {
		return getName();
	}
	JWTClaimType getType();
}

package com.divisors.projectcuttlefish.crypto.api.jose.jws;

import java.security.Key;

public interface Algorithm {
	boolean verify(Key key);
}

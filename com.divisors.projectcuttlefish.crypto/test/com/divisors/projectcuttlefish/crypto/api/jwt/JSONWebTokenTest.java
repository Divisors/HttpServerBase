package com.divisors.projectcuttlefish.crypto.api.jwt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.divisors.projectcuttlefish.crypto.api.jose.jwt.JSONWebToken;

public class JSONWebTokenTest {

	@Test
	public void test() throws Exception {
		JSONWebToken.parseJOSE("eyJhbGciOiJub25lIn0.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.");
		fail("Not yet implemented");
	}

}

package com.divisors.projectcuttlefish.crypto.api.jwt;

import java.util.Base64;

import org.json.JSONObject;

public class JSONWebToken {
	protected final JSONObject object;
	
	public JSONWebToken(String b64) {
		this.object = new JSONObject(Base64.getDecoder().decode(b64));
	}
	
	public String toBase64() {
		return null;
	}
}

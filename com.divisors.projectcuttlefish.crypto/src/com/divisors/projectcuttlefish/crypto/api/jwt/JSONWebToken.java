package com.divisors.projectcuttlefish.crypto.api.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONWebToken {
	
	public static JSONWebToken parseJOSE(String jose) throws JWTValidationException, JWTParsingException {
		Decoder b64decoder = Base64.getDecoder();
		String[] segments = jose.split("\\.", 5);
		if (segments.length != 3 && segments.length != 5)
			throw new JWTValidationException("Invalid # of segments: " + segments.length);
		
		final JSONObject joseHeader;
		try {
			String encodedHeader = segments[0];
			byte[] decodedUTF8 = b64decoder.decode(encodedHeader);
			String decodedHeader = new String(decodedUTF8, StandardCharsets.UTF_8);
			try {
				joseHeader = new JSONObject(decodedHeader);
			} catch (JSONException e) {
				throw new JWTParsingException("Invalid JSON object: " + decodedHeader, e);
			}
		} catch (Exception e) {
			throw new JWTParsingException(e);
		}
		
		System.out.println(joseHeader);
		
		//see http://tools.ietf.org/html/rfc7516#section-9
		final boolean isJWE = segments.length == 5;
		if (isJWE ^ (!joseHeader.has("enc"))) {
			if (isJWE)
				throw new JWTValidationException("JOSE has 5 segments, but contains no 'enc' member in its header");
			else
				throw new JWTValidationException("JOSE has 3 segments, but contains an 'enc' member in its header");
		}
		
		final String message;
		System.out.println("Is " + (isJWE ? "JWE" : "JWS"));
		
		if (isJWE) {
			//is JWE
			String encodedJWE = segments[0];
			byte[] decodedJWE = b64decoder.decode(encodedJWE);
			message = new String(decodedJWE, StandardCharsets.UTF_8);
		} else {
			
		}
		return null;
	}
	
	protected final JSONObject object;
	
	public JSONWebToken(String b64) {
		this.object = new JSONObject(Base64.getDecoder().decode(b64));
	}
	
	public String encode() {
		return null;
	}
	
	@Override
	public String toString() {
		return object.toString();
	}
}

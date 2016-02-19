package com.divisors.projectcuttlefish.crypto.api.jose.jwt;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	protected final Map<JWTClaim, Object> claims;
	
	public JSONWebToken(Map<JWTClaim, Object> claims) {
		this.claims = claims;
	}
	
	public String getClaim(String name) {
		return getClaim(JWTClaim.getFor(name, null));
	}
	public String getClaim(JWTClaim claim) {
		Object value = claims.get(claim);
		
		if (value == null)
			return null;
		
		if (value instanceof Collection) {
			Collection<?> values = (Collection<?>) value;
			if (values.isEmpty())
				return null;
			if (values.size() == 1)
				return values.iterator().next().toString();
		}
		return value.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getClaimList(JWTClaim claim) {
		Object value = claims.get(claim);
		
		if (value == null)
			return Collections.EMPTY_LIST;
		
		if (value instanceof String)
			return Arrays.asList(((String)value));
		
		if (value instanceof Object[]) {
			Object[] values = (Object[]) value;
			if (values.length == 0)
				return (List<String>) Collections.EMPTY_LIST;
			
			if (values.getClass().getComponentType().isAssignableFrom(String.class))
				return Arrays.asList(((String[]) values));
			
			List<String> result = new ArrayList<String>(values.length);
			for (Object v : values)
				result.add(v.toString());
			
			return result;
		}
		if (value instanceof Collection) {
			Collection<?> values = (Collection<?>) value;
			
			if (values.isEmpty())
				return Collections.EMPTY_LIST;
			
			//TODO check element type
			return new ArrayList<String>((Collection<String>)values);
		}
		
		return Arrays.asList(value.toString());
	}
	
	public void setClaim(String name, String...values) {
		claims.put(JWTClaim.getFor(name, null), values);
	}
	
	public void setClaim(JWTClaim claim, String...values) {
		switch (values.length) {
			case 0:
				claims.remove(claim);
				return;
			case 1:
				claims.put(claim, values[0]);
				return;
			default:
				claims.put(claim, Arrays.asList(values));
				return;
		}
	}
	
	public JSONObject toJSON() {
		return new JSONObject(claims);
	}
}

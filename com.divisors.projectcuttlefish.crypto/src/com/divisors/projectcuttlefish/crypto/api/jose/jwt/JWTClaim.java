package com.divisors.projectcuttlefish.crypto.api.jose.jwt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface JWTClaim {
	public static JWTClaim getFor(String name, JWTClaimType type) {
		return JWTClaims.getFor(name, type);
	}
	
	String getName();
	default String getPrettyName() {
		return getName();
	}
	JWTClaimType getType();
	
	public static final class JWTClaims {
		protected static final Map<String, JWTClaim> claims = new ConcurrentHashMap<>();
		
		public static JWTClaim getFor(final String name, final JWTClaimType type) {
			JWTClaim claim;
			
			if (claims.isEmpty())
				//Force classloader to load JWTRegisteredClaim
				JWTRegisteredClaim.values();
			
			//See if a claim has been registered under this name already
			if ((claim = claims.get(name)) != null)
				return claim;
			
			claim = new JWTClaim() {
				@Override
				public String getName() {
					return name;
				}

				@Override
				public JWTClaimType getType() {
					return (type != null) ? type : JWTClaimType.PUBLIC;
				}
			};
			
			registeredClaims.put(name, claim);
			
			return claim;
		}
	}
}

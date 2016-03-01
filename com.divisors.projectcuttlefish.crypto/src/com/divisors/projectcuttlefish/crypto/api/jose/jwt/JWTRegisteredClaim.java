package com.divisors.projectcuttlefish.crypto.api.jose.jwt;

/**
 * Enumeration of registered JWT claims
 * <p><blockquote>
 * The following Claim Names are registered in the IANA "JSON Web Token Claims"
 * registry established by Section 10.1. None of the claims defined below are
 * intended to be mandatory to use or implement in all cases, but rather they
 * provide a starting point for a set of useful, interoperable claims.
 * Applications using JWTs should define which specific claims they use and when
 * they are required or optional. All the names are short because a core goal of
 * JWTs is for the representation to be compact.
 * </blockquote></p>
 * 
 * From <a href="http://tools.ietf.org/html/rfc7519#section-4.1">RFC 7519 § 4.1</a>
 * 
 * @author mailmindlin
 */
public enum JWTRegisteredClaim implements JWTClaim {
	/**
	 * The "iss" (issuer) claim identifies the principal that issued the JWT.
	 * The processing of this claim is generally application specific. The "iss"
	 * value is a case-sensitive string containing a StringOrURI value. Use of
	 * this claim is OPTIONAL.
	 */
	ISSUER("iss"),
	/**
	 * The "sub" (subject) claim identifies the principal that is the subject of
	 * the JWT. The claims in a JWT are normally statements about the subject.
	 * The subject value MUST either be scoped to be locally unique in the
	 * context of the issuer or be globally unique. The processing of this claim
	 * is generally application specific. The "sub" value is a case-sensitive
	 * string containing a StringOrURI value. Use of this claim is OPTIONAL.
	 */
	SUBJECT("sub"),
	/**
	 * The "aud" (audience) claim identifies the recipients that the JWT is
	 * intended for. Each principal intended to process the JWT MUST identify
	 * itself with a value in the audience claim. If the principal processing
	 * the claim does not identify itself with a value in the "aud" claim when
	 * this claim is present, then the JWT MUST be rejected. In the general
	 * case, the "aud" value is an array of case- sensitive strings, each
	 * containing a StringOrURI value. In the special case when the JWT has one
	 * audience, the "aud" value MAY be a single case-sensitive string
	 * containing a StringOrURI value. The interpretation of audience values is
	 * generally application specific. Use of this claim is OPTIONAL.
	 */
	AUDIENCE("aud"),
	/**
	 * The "exp" (expiration time) claim identifies the expiration time on or
	 * after which the JWT MUST NOT be accepted for processing. The processing
	 * of the "exp" claim requires that the current date/time MUST be before the
	 * expiration date/time listed in the "exp" claim. Implementers MAY provide
	 * for some small leeway, usually no more than a few minutes, to account for
	 * clock skew. Its value MUST be a number containing a NumericDate value.
	 * Use of this claim is OPTIONAL.
	 */
	EXPIRATION_TIME("exp"),
	/**
	 * The "nbf" (not before) claim identifies the time before which the JWT
	 * MUST NOT be accepted for processing. The processing of the "nbf" claim
	 * requires that the current date/time MUST be after or equal to the
	 * not-before date/time listed in the "nbf" claim. Implementers MAY provide
	 * for some small leeway, usually no more than a few minutes, to account for
	 * clock skew. Its value MUST be a number containing a NumericDate value.
	 * Use of this claim is OPTIONAL.
	 */
	NOT_BEFORE("nbf"),
	/**
	 * The "iat" (issued at) claim identifies the time at which the JWT was
	 * issued. This claim can be used to determine the age of the JWT. Its value
	 * MUST be a number containing a NumericDate value. Use of this claim is
	 * OPTIONAL.
	 */
	ISSUED_AT("iat"),
	/**
	 * The "jti" (JWT ID) claim provides a unique identifier for the JWT. The
	 * identifier value MUST be assigned in a manner that ensures that there is
	 * a negligible probability that the same value will be accidentally
	 * assigned to a different data object; if the application uses multiple
	 * issuers, collisions MUST be prevented among values produced by different
	 * issuers as well. The "jti" claim can be used to prevent the JWT from
	 * being replayed. The "jti" value is a case- sensitive string. Use of this
	 * claim is OPTIONAL.
	 */
	JWT_ID("jti"),
	/**
	 * @deprecated went away as of jwt draft - 12; it's only a header parameter name
	 */
	@Deprecated
	TYPE("typ");
	
	protected final String name;
	
	JWTRegisteredClaim(String name) {
		this.name = name;
		
		//register self
		JWTClaims.claims.put(name, this);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getPrettyName() {
		return name().substring(0, 1) + name().toLowerCase().substring(1).replace('_', ' ');
	}
	
	@Override
	public JWTClaimType getType() {
		return JWTClaimType.REGISTERED;
	}
}

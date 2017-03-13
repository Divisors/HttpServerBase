package com.divisors.projectcuttlefish.uac.api.agent;

/**
 * Represents a 
 */
public class PcSession {
	protected final UUID sessionId;
	protected PcAgent agent;
	protected Instant expry;
	
	protected PcSession(UUID id, PcAgent agent, Instant expry) {
		this.sessionId = id;
		this.agent = agent;
		this.expry = expry;
	}
	/**
	 * Whether this session is still valid
	 */
	public boolean isValid() {
		return this.expry.isAfter(Instant.now());
	}
	
	public Integer getExpiration() {
		return this.expry;
	}
	
	/**
	 * Get the session id
	 */
	public UUID getId() {
		return this.sessionId;
	}
	
	/**
	 * Get the agent that this is a session for
	 */
	public PcAgent agent() {
		return this.agent;
	}
	
	/**
	 * Invalidate this session
	 */
	public void invalidate() {
		//TODO finish
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append("PcSession{id=").append(this.sessionId)
			.append(";expry=").append(this.expry)
			.append(";agent=").append(this.agent)
			.append("}").toString();
	}
}

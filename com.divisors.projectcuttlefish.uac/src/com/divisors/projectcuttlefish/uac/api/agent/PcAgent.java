package com.divisors.projectcuttlefish.uac.api.agent;

public abstract class PcAgent extends PcEntity {
	protected final UUID id;
	protected PcAgent(UUID id) {
		if (id == null)
			throw new IllegalArgumentException("UUID must not be null");
		this.id = id;
	}
	public final UUID getId() {
		return this.id;
	}
	public abstract boolean isAnonymous();
	public abstract boolean isHuman();
	@Override
	public String toString() {
		return getClass().getName()+"#"+getId()+'$'+(isAnonymous()?"ANONYMOUS":"")+getName()+(isAuthenticated()?"(authenticated)":"?")+"@"+hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PcAgent))
			return false;
		PcAgent agent = (PcAgent)other;
		return agent.getId().equals(getId());
	}
}
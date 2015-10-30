package com.divisors.projectcuttlefish.uac;

public abstract class User {
	abstract long getId();
	abstract String getName();
	abstract boolean isAnonymous();
	abstract boolean isAuthenticated();
	@Override
	public String toString() {
		return getClass().getName()+"#"+getId()+'$'+(isAnonymous()?"ANONYMOUS":"")+getName()+(isAuthenticated()?"(authenticated)":"?")+"@"+hashCode();
	}
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof User))
			return false;
		User u = (User)other;
		return u.getId() == getId() && u.isAnonymous() == isAnonymous() && u.isAuthenticated() == isAuthenticated();
	}
}

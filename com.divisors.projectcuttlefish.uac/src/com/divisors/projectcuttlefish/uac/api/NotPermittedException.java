package com.divisors.projectcuttlefish.uac.api;

public class NotPermittedException extends RuntimeException {
	private static final long serialVersionUID = -5092517600291707813L;
	
	protected final PcAgent agent;
	protected final String permission;
	
	public NotPermittedException() {
		super();
		this.user = null;
		this.permission = null;
	}
	
	public NotPermittedException(String problem) {
		super(problem);
		this.user = null;
		this.permission = null;
	}
	
	public NotPermittedException(PcAgent agent, String permission) {
		super(user.toString() + " requires permission '" + permission + '\'');
		this.agent = agent;
		this.permission = permission;
	}
}

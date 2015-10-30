package com.divisors.projectcuttlefish.uac;

public class NotPermittedException extends RuntimeException {
	private static final long serialVersionUID = -5092517600291707813L;
	
	protected final User user;
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
	
	public NotPermittedException(User user, String permission) {
		super(user.toString() + " requires permission '" + permission + '\'');
		this.user = user;
		this.permission = permission;
	}
}

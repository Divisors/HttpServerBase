package com.divisors.projectcuttlefish.uac.api.agent;

public class PcAnonymousUser extends PcUser {
	protected static final HumanName name = new HumanName(null, null, "Anon", "Y", "Mous");
	public PcAnonymousUser() {
		super(PcEntity.newEntityUUID());
	}
	public PcAnonymousUser(UUID id) {
		super(id);
		this.userName = "AnonymousUser$" + id.toString();
		this.humanName = name;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	@Override
	public boolean isAnonymous() {
		return true;
	}
}

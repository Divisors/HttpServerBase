package com.divisors.projectcuttlefish.uac.api.agent;

import java.util.UUID;

import com.divisors.projectcuttlefish.uac.api.humans.HumanName;

public class PcUser extends PcAgent {
	public static PcUser forName(String name) {
		
	}
	public static PcUser fromId(UUID id) {
		//TODO finish
		return null;
	}
	protected String userName;
	protected HumanName name;
	protected PcUser(UUID id) {
		
	}
	
	public HumanName getHumanName() {
		return this.name;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	@Override
	public boolean isHuman() {
		return true;
	}
	
	@Override
	public boolean isAnonymous() {
		return false;
	}
	@Override
	public UUID getEntityId() {
		// TODO Auto-generated method stub
		return null;
	}
}

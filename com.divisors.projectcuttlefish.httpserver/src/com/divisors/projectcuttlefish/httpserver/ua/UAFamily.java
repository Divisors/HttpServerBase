package com.divisors.projectcuttlefish.httpserver.ua;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * User agent software family.
 * 
 * Chrome on Windows, Chrome on Mac, and Chrome on Android would all be part of the 'Google Chrome' family.
 * @author mailmindlin
 */
public class UAFamily implements Externalizable {
	protected String familyName;
	protected UAFamily() {
		
	}
	public UAFamily(String name) {
		this.familyName = name;
	}
	public String getName() {
		return this.familyName;
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(familyName);
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		familyName = (String) in.readObject();
	}
}

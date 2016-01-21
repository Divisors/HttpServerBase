package com.divisors.projectcuttlefish.contentmanager.api;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceTag implements Externalizable {
	public static final Pattern parser = Pattern.compile("m(?<min>\\d+)(c(?<cmp>\\d+))?");
	protected int compression;
	protected int minimization;
	public ResourceTag(int compression, int minimization) {
		this.compression = compression;
		this.minimization = minimization;
	}
	public ResourceTag(String text) {
		if (text == null || text.isEmpty())
			throw new IllegalArgumentException("Illegal tag: " + text);
		if (text.equals("x")) {
			this.compression = 0;
			this.minimization = 0;
		} else {
			Matcher m = parser.matcher(text);
			if (!m.find())
				throw new IllegalArgumentException("Illegal tag: " + text);
			String min = m.group("min");
			String cmp = m.group("cmp");	
			minimization = Integer.parseInt(min);
			if (cmp == null)
				compression = 0;
			else
				compression = Integer.parseInt(cmp);
		}
	}
	public int getCompression() {
		return compression;
	}
	public int getMinimization() {
		return minimization;
	}
	@Override
	public String toString() {
		if (compression == 0 && minimization == 0)
			return "x";
		if (compression == 0)
			return "m"+minimization;
		return "m"+minimization+"c"+compression;
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(this.getMinimization());
		out.writeInt(this.getCompression());
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.minimization = in.readInt();
		this.compression = in.readInt();
	}
}

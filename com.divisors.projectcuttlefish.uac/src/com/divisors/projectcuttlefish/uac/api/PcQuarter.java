package com.divisors.projectcuttlefish.uac.api;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PcQuarter implements PcEntity {
	protected PcSchoolYear year;
	protected PcQuarterName name;
	public PcQuarter(int year, PcQuarterName name) {
		this.year = PcSchoolYear.of(year);
		this.name = name;
	}
	public PcQuarter(PcSchoolYear year, PcQuarterName name) {
		this.year = year;
		this.name = name;
	}
	
	public PcSchoolYear getYear() {
		return year;
	}
	public PcQuarterName getQuarterName() {
		return name;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.year = PcSchoolYear.of(in.readInt());
		this.name = PcQuarterName.values()[in.readInt()];
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(this.year.getStartYearNumber());
		out.writeInt(this.name.ordinal());
	}
	
	@Override
	public String toString() {
		return "Quarter " + (name.ordinal() + 1) + " of " + year;
	}

	public static enum PcQuarterName {
		Q1,
		Q2,
		Q3,
		Q4,
		SUMMER;
	}
}

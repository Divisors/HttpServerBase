package com.divisors.projectcuttlefish.uac.api;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.ref.WeakReference;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.divisors.projectcuttlefish.uac.api.PcQuarter.PcQuarterName;

public class PcSchoolYear implements PcEntity {
	//TODO maybe ConcurrentHashMap
	protected static final HashMap<Year, WeakReference<PcSchoolYear>> pool = new HashMap<>();
	public static PcSchoolYear of(Year year) {
		if (pool.containsKey(year)) {
			PcSchoolYear result = pool.get(year).get();
			if (result != null)
				return result;
		}
		PcSchoolYear result = new PcSchoolYear(year);
		pool.put(year, new WeakReference<>(result));
		return result;
	}
	
	public static PcSchoolYear of(int yearNum) {
		Year year = Year.of(yearNum);
		if (pool.containsKey(year)) {
			PcSchoolYear result = pool.get(year).get();
			if (result != null)
				return result;
		}
		PcSchoolYear result = new PcSchoolYear(year);
		pool.put(year, new WeakReference<>(result));
		return result;
	}
	
	protected Year startYear;
	protected Set<PcQuarter> quarters = new HashSet<>();
	
	protected PcSchoolYear() {
		
	}
	
	public PcSchoolYear(Year y) {
		this.startYear = y;
	}
	
	public int getStartYearNumber() {
		return startYear.getValue();
	}
	public Set<PcQuarter> getQuarters() {
		if (quarters.isEmpty()) {
			quarters.add(new PcQuarter(this, PcQuarterName.Q1));
			quarters.add(new PcQuarter(this, PcQuarterName.Q2));
			quarters.add(new PcQuarter(this, PcQuarterName.Q3));
			quarters.add(new PcQuarter(this, PcQuarterName.Q4));
			quarters.add(new PcQuarter(this, PcQuarterName.SUMMER));
		}
		return new HashSet<>(this.quarters);
	}
	
	@Override
	public String toString() {
		int tmp = getStartYearNumber();
		return "Year of " + tmp + " to " + (tmp + 1);
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.startYear = Year.of(in.readInt());
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(this.startYear.getValue());
	}

}

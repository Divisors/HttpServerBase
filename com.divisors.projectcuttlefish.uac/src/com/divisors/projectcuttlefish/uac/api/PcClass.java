package com.divisors.projectcuttlefish.uac.api;

public PcClass implements Externalizable {
	public int getID() {
		//TODO finish
		return -1;
	}
	public String getName() {
		//TODO finish
		return null;
	}
	public PcTeacher getTeacher() {
		//TODO finish
		return null;
	}
	public Set<PcStudent> getStudents() {
		//TODO finish
		return null;
	}
	public Set<PcAssignment> getAssignmentsBetween(Instant start, Period length) {
		//TODO finish
		return null;
	}
	public Set<PcAssignment> getAssignments() {
		//TODO finish
		return null;
	}
	public boolean isImmutable() {
		//TODO finish
		return true;
	}
	public PcClass setTeacher(PcUser modifier, PcTeacher teacher) throws InvalidPermissionException, ImmutableException {
		//TODO finish
		return null;
	}
	public PcClass addStudent(PcUser modifier, PcStudent student) throws InvalidPermissionException, ImmutableException {
		//TODO finish
		return null;
	}
	public PcClass removeStudent(PcUser modifier, PcStudent student) throws InvalidPermissionException, ImmutableException {
		//TODO finish
		return null;
	}
	public PcQuarter getStartQuarter() {
		//TODO finish
		return null;
	}
	public PcQuarter getEndQuarter() {
		//TODO finish
		return null;
	}
	public double getCredits() {
		//TODO finish
		return 0.0;
	}
	public PcClass setCredits(PcUser modifier, double credits) throws InvalidPermissionException, ImmutableException {
		//TODO finish
		return null;
	}
	
}

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
		PermissiblePcClasssAddStudentAction action = new PermissiblePcClassAddStudentAction(modifier, agent student);
		//TODO finish
		return null;
	}
	public PcClass removeStudent(PcUser modifier, PcStudent student) throws InvalidPermissionException, ImmutableException {
		PermissiblePcClasssRemoveStudentAction action = new PermissiblePcClassRemoveStudentAction(modifier, agent student);
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
		PermissiblePcClassModifyAction action = new PermissiblePcClassModifyAction(modifier, this, "credits", this.getCredits(), credits);
		//TODO finish
		return null;
	}
	public static PermissiblePcClassCreateAction extends PermissibleAction {
		public PermissiblePcClassCreateAction(PcUser agent, PcClass created) {
			super(PermissibleAction.PermissibleActionType.CREATE, agent, created);
		}
	}
	public static PermissiblePcClassAddStudentAction extends PermissibleAction {
		protected final PcStudent student;
		public PermissiblePcClassAddStudentAction(PcUser agent, PcClass target, PcStudent added) {
			super(PermissibleAction.PermissibleActionType.ADD, agent, target);
			this.student = added;
		}
		public PcStudent getStudent() {
			return student;
		}
	}
	public static PermissiblePcClassRemoveStudentAction extends PermissibleAction {
		public PermissiblePcClassRemoveStudentAction(PcUser agent, PcClass target) {
			super(PermissibleAction.PermissibleActionType.REMOVE, agent, target);
		}
	}
	public static PermissiblePcClassModifyAction extends PermissibleAction {
		protected final String field;
		protected final Object oldValue, newValue;
		public PermissiblePcClassModifyAction(PcUser agent, PcClass target, String field, Object oldValue, Object newValue) {
			super(PermissibleAction.PermissibleActionType.MODIFY, agent, target);
			this.field = field;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
}

package com.divisors.projectcuttlefish.uac.api;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.Instant;
import java.time.Period;
import java.util.Set;

public abstract class PcClass implements PcEntity {
	public abstract int getID();
	public abstract String getName();
	public abstract PcTeacher getTeacher();
	public abstract Set<PcStudent> getStudents();
	/**
	 * Returns assigmnents due between dates
	 * @param start
	 * @param length
	 * @return
	 */
	public Set<PcAssignment> getAssignmentsBetween(Instant start, Period length) {
		Set<PcAssignment> result = getAssignments();
		Instant end = start.plus(length);
		result.removeIf((assignment)->{
			Instant due = assignment.getDue();
			return !(due.isAfter(start) && due.isBefore(end));
		});
		return result;
	}
	public abstract Set<PcAssignment> getAssignments();
	public boolean isImmutable() {
		return true;
	}
	public PcClass setTeacher(PcUser modifier, PcTeacher teacher) throws InvalidPermissionException, ImmutableException {
		//TODO finish
		return null;
	}
	public PcClass addStudent(PcUser agent, PcStudent student) throws InvalidPermissionException, ImmutableException {
		PermissiblePcClassAddStudentAction action = new PermissiblePcClassAddStudentAction(agent, this, student);
		//TODO finish
		return null;
	}
	public PcClass removeStudent(PcUser agent, PcStudent student) throws InvalidPermissionException, ImmutableException {
		PermissiblePcClassRemoveStudentAction action = new PermissiblePcClassRemoveStudentAction(agent, this, student);
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
	public class PermissiblePcClassCreateAction extends PermissibleAction {
		public PermissiblePcClassCreateAction(PcUser agent, PcClass created) {
			super(PermissibleAction.PermissibleActionType.CREATE, agent, created);
		}
	}
	public class PermissiblePcClassAddStudentAction extends PermissibleAction {
		protected final PcStudent student;
		public PermissiblePcClassAddStudentAction(PcUser agent, PcClass target, PcStudent added) {
			super(PermissibleAction.PermissibleActionType.ADD, agent, target);
			this.student = added;
		}
		public PcStudent getStudent() {
			return student;
		}
	}
	public static class PermissiblePcClassRemoveStudentAction extends PermissibleAction {
		protected final PcStudent student;
		public PermissiblePcClassRemoveStudentAction(PcUser agent, PcClass target, PcStudent removed) {
			super(PermissibleAction.PermissibleActionType.REMOVE, agent, target);
			this.student = removed;
		}
	}
	public static class PermissiblePcClassModifyAction extends PermissibleAction {
		protected final String field;
		protected final Object oldValue, newValue;
		public PermissiblePcClassModifyAction(PcUser agent, PcClass target, String field, Object oldValue, Object newValue) {
			super(PermissibleAction.PermissibleActionType.MODIFY, agent, target);
			this.field = field;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			// TODO Auto-generated method stub
			
		}
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		
	}
}

package com.divisors.projectcuttlefish.uac.api;

public abstract class PermissableAction implements Externalizable, JSONifiable {
	protected final PcUser agent;
	protected final PcEntity target;
	protected final PermissableActionType type;
	
	protected PermissableAction(PermissableActionType type, PcUser agent, PcEntity target) {
		this.type = type;
		this.agent = agent;
		this.target = target;
	}
	
	public PcEntity getTarget() {
		return this.target;
	}
	public PermissableActionType getType() {
		return this.type;
	}
	public PcUser getAgent() {
		return this.agent;
	}
	
	public enum PermissableActionType {
		/**
		 * Action when some object is created. For example, a new class is created.
		 */
		CREATE,
		/**
		 * Action when some object is deleted. For example, a club is deleted.
		 */
		DELETE,
		/**
		 * Action when a field of an entity is modified. For example, the due date of an assignment is changed.
		 */
		MODIFY,
		/**
		 * Action when an entity is added to a collection. For example, a student is added to a class.
		 */
		ADD,
		/**
		 * An action when an entity is
		 */
		REMOVE,
		CREATE_ADD,
		REMOVE_DELETE,
		/**
		 * A low-priority CREATE_ADD. For example, a student comments on a forum.
		 */
		COMMENT,
		/**
		 * When someone access some protected information
		 */
		ACCESS,
		/**
		 * When a PURGE event is forcibly triggered.
		 */
		PURGE;
	}
	
	/**
	 * A series of actions carried out in series. Basically a transaction, so if any of the sub-events fails, no
	 * change is made to anything.
	 */
	public static class PermissableActionSeries extends PermissableAction {
		
	}
}

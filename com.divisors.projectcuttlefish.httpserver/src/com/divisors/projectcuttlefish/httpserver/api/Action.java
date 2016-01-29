package com.divisors.projectcuttlefish.httpserver.api;

import com.divisors.projectcuttlefish.httpserver.api.error.ActionUnavailableException;

/**
 * Essentially a function handle, but specifies whether it is still valid
 * @author mailmindlin
 */
@FunctionalInterface
public interface Action {
	/**
	 * Creates an action from a runnable, mapping {@link Runnable#run()} to {@link Action#act()}.
	 * @param r runnable
	 * @return action
	 */
	public static Action from(Runnable r) {
		return r::run;
	}
	/**
	 * Whether the action is currently available
	 * @return availability of action
	 */
	default boolean isAvailable() {
		return true;
	}
	/**
	 * Runs the action
	 * @throws ActionUnavailableException if the action is unavailable
	 */
	void act() throws ActionUnavailableException;
	/**
	 * Creates a composite action, where the other action immediately follows this one
	 * @param other action to composite with this one, to occur after
	 * @return composite action
	 * @see ActionSeries
	 */
	default Action andThen(Action other) {
		return new ActionSeries(this, other);
	}
	/**
	 * Denotes a composited series of actions
	 * @author mailmindlin
	 */
	public static final class ActionSeries implements Action {
		/**
		 * Actions that are called in order
		 */
		protected final Action[] actions;
		public ActionSeries(Action...actions) {
			this.actions = actions;
		}
		@Override
		public void act() throws ActionUnavailableException {
			for (Action action : actions)
				action.act();
		}
		
		@Override
		public boolean isAvailable() {
			for (Action action : actions)
				if (!action.isAvailable())
					return false;
			return true;
		}
		@Override
		public ActionSeries andThen(Action other) {
			if (other instanceof ActionSeries) {
				ActionSeries otherSeries = (ActionSeries) other;
				Action[] sum = new Action[this.actions.length + otherSeries.actions.length];
				System.arraycopy(this.actions, 0, sum, 0, this.actions.length);
				System.arraycopy(otherSeries.actions, 0, sum, this.actions.length, otherSeries.actions.length);
				return new ActionSeries(sum);
			}
			
			Action[] sum = new Action[this.actions.length + 1];
			sum[this.actions.length] = other;//sum[this.actions.length] is the last index in sum
			System.arraycopy(this.actions, 0, sum, 1, this.actions.length);
			return new ActionSeries(sum);
		}
	}
}

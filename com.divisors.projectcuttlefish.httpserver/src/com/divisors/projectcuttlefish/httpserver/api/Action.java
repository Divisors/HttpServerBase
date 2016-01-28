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
}

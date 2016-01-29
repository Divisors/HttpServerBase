package com.divisors.projectcuttlefish.httpserver.util;

import java.lang.ref.WeakReference;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.error.ActionUnavailableException;

import reactor.bus.registry.Registration;

/**
 * An action for the cancellation of a registration.
 * @author mailmindlin
 */
public class RegistrationCancelAction implements Action {
	/**
	 * A reference to the registration
	 */
	protected final WeakReference<Registration<?, ?>> registration;
	/**
	 * Create with given registration
	 * @param registration registration to cancel
	 */
	public RegistrationCancelAction(Registration<?, ?> registration) {
		this.registration = new WeakReference<>(registration);
	}
	/**
	 * Whether this action is currently available. Will probably (assume always) return
	 * true if the registration hasn't been cancelled.
	 * @return the availability of this action
	 */
	@Override
	public boolean isAvailable() {
		Registration<?,?> registration = this.registration.get();
		return !(registration == null || registration.isCancelled());
	}
	/**
	 * Cancel the registration
	 * @throws ActionUnavailableException if cancelling the registration isn't a thing that you can do now
	 */
	@Override
	public void act() throws ActionUnavailableException {
		Registration<?,?> registration = this.registration.get();
		if (registration == null || registration.isCancelled())
			throw new ActionUnavailableException("The registration has already been cancelled.");
		registration.cancelAfterUse();
		this.registration.clear();
	}

}

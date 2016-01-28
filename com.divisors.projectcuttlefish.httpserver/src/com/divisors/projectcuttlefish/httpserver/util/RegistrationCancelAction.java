package com.divisors.projectcuttlefish.httpserver.util;

import java.lang.ref.WeakReference;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.error.ActionUnavailableException;

import reactor.bus.Event;
import reactor.bus.registry.Registration;
import reactor.fn.Consumer;

public class RegistrationCancelAction implements Action {
	protected final WeakReference<Registration<?, ?>> registration;
	public RegistrationCancelAction(Registration<?, ?> registration) {
		this.registration = new WeakReference<>(registration);
	}
	@Override
	public boolean isAvailable() {
		Registration<?,?> registration = this.registration.get();
		return !(registration == null || registration.isCancelled());
	}
	@Override
	public void act() throws ActionUnavailableException {
		Registration<?,?> registration = this.registration.get();
		if (registration == null || registration.isCancelled())
			throw new ActionUnavailableException("The registration has already been cancelled.");
		registration.cancelAfterUse();
		this.registration.clear();
	}

}

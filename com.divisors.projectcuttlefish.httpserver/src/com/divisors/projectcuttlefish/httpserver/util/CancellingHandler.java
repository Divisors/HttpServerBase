package com.divisors.projectcuttlefish.httpserver.util;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.registry.Registration;
import reactor.bus.selector.Selector;

public class CancellingHandler {
	public static void bind(EventBus bus, Selector<?> selector, Predicate<Event<?>> handler) {
		final CancellingHandler canceller = new CancellingHandler();
		canceller.setRegistration(bus.on(selector, (event) -> {
			if (!handler.test(event))
				canceller.cancel();
		}));
	}
	protected WeakReference<Registration<Object, ?>> reg;
	protected boolean willCancel = false;
	public void cancel() {
		if (reg != null)
			reg.get().cancel();
		else
			willCancel = true;
	}
	protected void setRegistration(Registration<Object, ?> reg) {
		if (willCancel)
			reg.cancel();
		else
			this.reg = new WeakReference<>(null);
	}
}

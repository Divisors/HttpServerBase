package com.divisors.projectcuttlefish.contentmanager.api.plugins;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import reactor.bus.Event;
import reactor.fn.Consumer;

/**
 * 
 * @author mailmindlin
 *
 */
public interface EventTarget {
	default boolean addEventListener(String eventName, Object handler) {
		final Method callMethod;
		try {
			callMethod = handler.getClass().getMethod("call", new Class<?>[]{Object.class, Object[].class});
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return false;
		}
		addEventListener(eventName, event-> {
			Object data = event.getData();
			Object[] args;
			if (data == null) {
				args = new Object[]{handler, new Object[0]};
			} else if (data.getClass().isArray()) {
				args = new Object[]{handler, ((Object[]) data)};
			} else {
				args = new Object[]{handler, new Object[]{data}};
			}
			try {
				callMethod.invoke(handler, args);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw e;
			}
		});
		return true;
	}
	default void addEventListener(String eventName, Consumer<? extends Event<?>> handler) {
		addEventListener(eventName, handler, false);
	}
	/**
	 * 
	 * @param event event name
	 * @param handler handler function
	 * @param onlyOnce whether to automatically deregister the event handler after use
	 */
	void addEventListener(String eventName, Consumer<? extends Event<?>> handler, boolean onlyOnce);
}

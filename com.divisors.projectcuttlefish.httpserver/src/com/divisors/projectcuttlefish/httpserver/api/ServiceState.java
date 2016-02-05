package com.divisors.projectcuttlefish.httpserver.api;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import com.divisors.projectcuttlefish.httpserver.api.error.ServiceStateException;

/**
 * An enumeration of states available for a {@link RunnableService},
 * @author mailmindlin
 */
public enum ServiceState {
	UNINITIALIZED,
	INITIALIZED,
	STARTING,
	RUNNING,
	STOPPING,
	DESTROYED,
	UNKNOWN,
	OTHER;
	/**
	 * Assert that the state is the same as expected
	 * @param test the state to test
	 * @param expect the expected state
	 * @throws ServiceStateException if the state differs from the expected state
	 * @return true, if <code>test == expect</code>
	 */
	public static boolean assertEquals(ServiceState test, ServiceState expect) throws ServiceStateException {
		if (test != expect)
			throw new ServiceStateException(expect, test);
		return true;
	}
	/**
	 * Assert that the state is one of a set of states
	 * @param test state to test
	 * @param expect list of expected states
	 * @throws ServiceStateException
	 * @return true, if <code>test</code> is in <code>expect</code>
	 */
	public static boolean assertAny(final ServiceState test, final ServiceState...expect) throws ServiceStateException {
		for (ServiceState state : expect)
			if (test == state)
				return true;
		throw new ServiceStateException(String.format("EXPECT ANY: %s; ACTUAL: %s", Arrays.toString(expect), test), null, test);
	}
	/**
	 * Assert that the state is not in a set of states
	 * @param test actual state to test
	 * @param dontExpect list of unexpected states
	 * @throws ServiceStateException if the <code>test</code> is found in the list of unexpected states
	 * @return true if <code>test</code> is not found
	 */
	public static boolean assertNone(ServiceState test, ServiceState...dontExpect) throws ServiceStateException {
		for (ServiceState state : dontExpect)
			if (test == state)
				throw new ServiceStateException(String.format("EXPECT NOT %s", state.name()), state, state);
		return true;
	}
	/**
	 * Like {@link AtomicReference#compareAndSet(Object, Object) AtomicReference.compareAndSet(ServiceState, ServiceState)},
	 * but throws an error if it fails.
	 * @param test
	 * @param expect
	 * @param set
	 * @throws ServiceStateException
	 */
	public static boolean assertAndSet(AtomicReference<ServiceState> test, ServiceState expect, ServiceState set) throws ServiceStateException {
		if (!test.compareAndSet(expect, set))
			throw new ServiceStateException(expect, test.get());
		return true;
	}
	/**
	 * Like {@link AtomicReference#compareAndSet(Object, Object) AtomicReference.compareAndSet(ServiceState, ServiceState)},
	 * but throws an error if it fails for all expected states
	 * @param test state to test and update
	 * @param expect set of expected states
	 * @param set value to set <code>test</code> to, if its current value is found in <code>expect</code>
	 * @return true, if <code>test</code> is found in <code>expect</code>
	 * @throws ServiceStateException if the <code>test</code> is not one of <code>expect</code>
	 */
	public static boolean assertAndSetAny(AtomicReference<ServiceState> test, ServiceState[] expect, ServiceState set) throws ServiceStateException {
		for (ServiceState state : expect)
			if (test.compareAndSet(state, set))
				return true;
		throw new ServiceStateException(String.format("EXPECT ANY: %s; ACTUAL: %s", Arrays.toString(expect), test.get(), test.get()), null, test.get());
	}
}

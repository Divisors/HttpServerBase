package com.divisors.projectcuttlefish.httpserver.api;

/**
 * 
 * @author mailmindlin
 *
 * @param <E> immutable type
 */
public interface Mutable<E extends Mutable<E>> {
	/**
	 * Whether this object has fields that can be changed.
	 * @return that
	 */
	boolean isMutable();
	/**
	 * Get an immutable object. The object returned MAY only be shallowly immutable (the
	 * properties of its properties may be mutable). If {@link #isMutable()} is true for 
	 * this object, then it MAY return itself.
	 * @return Immutable object with same data
	 */
	E immutable();
}

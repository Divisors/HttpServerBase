package com.divisors.projectcuttlefish.httpserver.api;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Method;

import sun.misc.Unsafe;

public class AtomicEnum<E extends Enum<E>> implements Externalizable {
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
	private static final Method enumGetValues;
	static {
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicEnum.class.getDeclaredField("value"));
			enumGetValues = Enum.class.getMethod("values");
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static final long serialVersionUID = 4662333666995087602L;
	private volatile E value;
	private Class<E> enumType;

	public AtomicEnum() {
		value = null;
	}
	public AtomicEnum(E initialValue) {
		this.enumType = initialValue.getDeclaringClass();
		this.value = initialValue;
	}
	public final E get() {
		return value;
	}
	public final void set(E newValue) {
		this.value = newValue;
	}
	public final void lazySet(E newValue) {
		unsafe.putOrderedObject(this, valueOffset, newValue);
	}
	public final boolean compareAndSet(E expect, E update) {
		return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
	}
	@Override
	public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
		
	}
	@Override
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
}

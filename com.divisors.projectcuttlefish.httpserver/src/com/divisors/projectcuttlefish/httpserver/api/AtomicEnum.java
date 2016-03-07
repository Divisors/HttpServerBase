package com.divisors.projectcuttlefish.httpserver.api;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;

import sun.misc.Unsafe;

public class AtomicEnum<E extends Enum<E>> implements Externalizable {
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
	static {
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicEnum.class.getDeclaredField("value"));
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	private static final long serialVersionUID = 4662333666995087602L;
	private volatile E value;
	private Class<E> enumType;

	public AtomicEnum() {
		this.enumType = null;
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
	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		int idx = input.readInt();
		if (idx > -2) {
			this.enumType = (Class<E>) input.readObject();
			if (idx >= 0) {
				try {
					this.value = ((E[])(enumType.getMethod("values").invoke(null)))[idx];
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					throw new IOException(e);
				}
			}
		}
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (value == null) {
			if (enumType == null) {
				out.writeInt(-2);
			} else {
				out.writeInt(-1);
				out.writeObject(enumType);
			}
		} else {
			out.writeInt(value.ordinal());
			out.writeObject(enumType);
		}
	}
}

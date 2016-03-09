package com.divisors.projectcuttlefish.uac.api.serial;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SerialUtil {
	public static void writeKnownObject(ObjectOutput out, Externalizable value) throws IOException {
		value.writeExternal(out);
	}
	public static <E extends Externalizable> E readKnownObject(ObjectInput in, Class<? extends E> inClass) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, InvocationTargetException {
		E result;
		try {
			result = inClass.newInstance();
		} catch (IllegalAccessException e0) {
			try {
				Constructor<? extends E> constructor = inClass.getConstructor();
				if (!constructor.isAccessible())
					constructor.setAccessible(true);
				result = constructor.newInstance();
			} catch (NoSuchMethodException | SecurityException e1) {
				Constructor<? extends E> constructor;
				try {
					constructor = inClass.getDeclaredConstructor();
				} catch (NoSuchMethodException e2) {
					throw new InstantiationException("Unable to find nullary constructor in class " + inClass);
				}
				if (!constructor.isAccessible())
					constructor.setAccessible(true);
				result = constructor.newInstance();
			}
		}
		result.readExternal(in);
		return result;
	}
}

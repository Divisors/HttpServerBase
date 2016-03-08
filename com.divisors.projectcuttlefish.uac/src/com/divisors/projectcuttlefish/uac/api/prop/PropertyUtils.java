package com.divisors.projectcuttlefish.uac.api.prop;

class PropertyUtils {
	protected static final Unsafe unsafe;
	static {
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			PropertyUtils.unsafe = singleoneInstanceField.get(null);
		} catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static Unsafe getUnsafe() {
		return unsafe;
	}
}

package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.function.Predicate;

public class UAOperatingSystem implements Comparable<UAOperatingSystem>, Predicate<String> {
	public static final UAOperatingSystem
		WINDOWS_VISTA = new UAOperatingSystem("Windows Vista", UAOSFamily.WINDOWS, new int[]{6,0}),
		WINDOWS_7 = new UAOperatingSystem("Windows 7", UAOSFamily.WINDOWS, new int[]{6,1}),
		WINDOWS_8 = new UAOperatingSystem("Windows 8", UAOSFamily.WINDOWS, new int[]{6,2}),
		WINDOWS_8_1 = new UAOperatingSystem("Windows 8.1", UAOSFamily.WINDOWS, new int[]{6,3}),
		WINDOWS_10 = new UAOperatingSystem("Windows 10", UAOSFamily.WINDOWS, new int[]{10,0})
			;
	final String name;
	final UAOSFamily family;
	final int[] version;
	public UAOperatingSystem(String name, UAOSFamily family, int[] version) {
		this.name = name;
		this.family = family;
		this.version = version;
	}
	@Override
	public int compareTo(UAOperatingSystem o) {
		if (family != o.family)
			return 0;
		for (int i=0; i<Math.min(version.length, o.version.length); i++)
			if (version[i] != o.version[i])
				return version[i] - o.version[i];
		return 0;
	}
	@Override
	public boolean test(String t) {
		// TODO Auto-generated method stub
		return false;
	}
}

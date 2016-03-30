package com.divisors.projectcuttlefish.contentmanager.api.plugins;

public class Console {
	protected static final Console INSTANCE = new Console();
	String prefix = "";
	int level = 0;
	public static Console getInstance() {
		return INSTANCE;
	}
	public void error(String msg, Object...args) {
		System.err.format(prefix + msg, args);
	}
	public void log(String msg, Object...args) {
		System.out.format(prefix + msg, args);
	}
	public void group() {
		level++;
		if (level == 1) {
			prefix = " ";
		} else {
			prefix = prefix.substring(0, prefix.length() - 1);
			System.out.println(prefix + "└");
			prefix += "  ";
		}
		System.out.println("━┐");
		prefix += "├";
	}
	public void group(String name) {
		level++;
		if (level == 1) {
			prefix = " ";
		} else {
			prefix = prefix.substring(0, prefix.length() - 1);
			System.out.print(prefix + "└");
			prefix += "  ";
		}
		System.out.println("━┬" + name);
		prefix += "├";
	}
	public void ungroup() {
		level--;
		if (level < 0) {
			level = 0;
			return;
		}
		if (level > 1) {
			System.out.print(prefix + "");
		}
	}
}

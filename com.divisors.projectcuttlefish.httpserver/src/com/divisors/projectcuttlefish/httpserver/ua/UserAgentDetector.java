package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgentDetector implements Function<String, UserAgent> {
	List<UAPattern> patterns = new LinkedList<UAPattern>();
	public void registerPattern(String name, String pattern) {
		this.patterns.add(new UAPattern(name, pattern));
	}
	@Override
	public UserAgent apply(String s) {
		for (UAPattern pattern : patterns) {
			Matcher m = pattern.pattern.matcher(s);
			if (m.find()) {
				System.out.println(pattern.name);
				return null;
			}
		}
		return null;
	}
	static class UAPattern {
		public String name;
		public Pattern pattern;
		public UAPattern(String name, String pattern) {
			this.name = name;
			this.pattern = Pattern.compile(pattern);
		}
	}
}

package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.Map;
import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;

@FunctionalInterface
public interface UserAgentParsingRule extends BiConsumer<ParsedUAToken[], Map<String, String>> {
	@Override
	void accept(final ParsedUAToken[] tokens, Map<String, String> result);
	
	public static class UAVersionParsingRule implements UserAgentParsingRule {
		protected final int token;
		protected final String key;
		public UAVersionParsingRule(int token, String key) {
			this.token = token;
			this.key = key;
		}
		@Override
		public void accept(ParsedUAToken[] tokens, Map<String, String> result) {
			//TODO finish
		}
		
	}
}

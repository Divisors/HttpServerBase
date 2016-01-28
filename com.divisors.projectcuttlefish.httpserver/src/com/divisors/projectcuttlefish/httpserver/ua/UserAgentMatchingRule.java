package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.function.Predicate;

import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;

@FunctionalInterface
public interface UserAgentMatchingRule extends Predicate<ParsedUAToken[]> {
	/**
	 * Generate a (mostly) efficient predicate based on a JSON rule 
	 * @param rule
	 * @return
	 */
	public static UserAgentMatchingRule compileJSON(JSONObject rule) {
		if (rule.has("token")) {
			int token = rule.getInt("token");
			boolean hasName = rule.has("productName");
			boolean hasVrsn = rule.has("productVersion");
			String name = null, version = null;
			if (hasName)
				name = rule.getString("productName");
			if (hasVrsn)
				version = rule.getString("productVersion");
			if (rule.has("product")) {
				hasName = true;
				hasVrsn = true;
				String[] product = rule.getString("product").split("/");
				name = product[0];
				version = product[1];
			}
			if (hasName && hasVrsn) {
				System.out.println("\tLoading rule | token: " + token + ", name: " + name + ", version: " + version);
				return new UAProductMatchingRule(token, name, version);
			} else if (hasName) {
				System.out.println("\tLoading rule | token: " + token + ", name: " + name);
				return new UAProductNameMatchingRule(token, name);
			} else if (hasVrsn) {
				System.out.println("\tLoading rule | token: " + token + ", version: " + version);
				return new UAProductVersionMatchingRule(token, version);
			}
			if (rule.has("detail")) {
				int detail = rule.getInt("detail");
				String value = rule.getString("value");
				System.out.println("\tLoading rule | token: " + token + ", detail: " + detail + ", value: " + value);
				return new UADetailMatchingRule(token, detail, value);
			}
		}
		System.err.println("Could not compile rule: " + rule);
		return null;
	}
	default boolean isRequired() {
		return true;
	}
	/**
	 * Whether a tokenized UA string that passes this test will ALWAYS pass the other test.
	 * <br/>
	 * If you're not sure, just return false.
	 * @param otherRule
	 * @return
	 */
	default boolean canSupersede(UserAgentMatchingRule otherRule) {
		return false;
	}
	@Override
	public boolean test(ParsedUAToken[] tokens);
	default UserAgentMatchingRule andThen(UserAgentMatchingRule other) {
		//TODO finish
		return null;
	}
	
	public static class UAProductNameMatchingRule implements UserAgentMatchingRule {
		public final int tokenNumber;
		public final String name;
		public UAProductNameMatchingRule(int token, String name) {
			this.tokenNumber = token;
			this.name = name;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			return (tokens.length > tokenNumber) && this.name.equals(tokens[tokenNumber].name);
		}
		@Override
		public boolean canSupersede(UserAgentMatchingRule otherRule) {
			return false;//TODO finish
		}
		@Override
		public UserAgentMatchingRule andThen(UserAgentMatchingRule other) {
			if (other instanceof UAProductVersionMatchingRule && ((UAProductVersionMatchingRule)other).tokenNumber == this.tokenNumber)
				return new UAProductMatchingRule(this.tokenNumber, this.name, ((UAProductVersionMatchingRule) other).version);
			return null; //TODO finish
		}
	}
	public static class UAProductVersionMatchingRule implements UserAgentMatchingRule {
		public final int tokenNumber;
		public final String version;
		public UAProductVersionMatchingRule(int token, String version) {
			this.tokenNumber = token;
			this.version = version;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			return (tokens.length > tokenNumber ) && this.version.equals(tokens[tokenNumber].version);
		}
		@Override
		public boolean canSupersede(UserAgentMatchingRule otherRule) {
			return false;//TODO finish
		}
	}
	public static class UAProductMatchingRule implements UserAgentMatchingRule {
		public final int tokenNumber;
		public final String name;
		public final String version;
		public UAProductMatchingRule(int token, String name, String version) {
			this.tokenNumber = token;
			this.name = name;
			this.version = version;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			return (tokens.length > tokenNumber) && this.name.equals(tokens[tokenNumber].name) && this.version.equals(tokens[tokenNumber].version);
		}
		@Override
		public boolean canSupersede(UserAgentMatchingRule otherRule) {
			if (otherRule instanceof UAProductNameMatchingRule) {
				UAProductNameMatchingRule other = (UAProductNameMatchingRule) otherRule;
				if (other.tokenNumber == this.tokenNumber && this.name.equals(other.name))
					return true;
			}
			if (otherRule instanceof UAProductVersionMatchingRule) {
				UAProductVersionMatchingRule other = (UAProductVersionMatchingRule) otherRule;
				if (other.tokenNumber == this.tokenNumber && this.version.equals(other.version))
					return true;
			}
			//TODO: finish
			return false;
		}
	}
	public static class UADetailMatchingRule implements UserAgentMatchingRule {
		public final int token;
		public final int detail;
		public final String value;
		public UADetailMatchingRule(int token, int detail, String value) {
			this.token = token;
			this.detail = detail;
			this.value = value;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			return (tokens.length > token) && (tokens[token].details.length > detail) && this.value.equals(tokens[token].details[detail]);
		}
		@Override
		public boolean canSupersede(UserAgentMatchingRule otherRule) {
			//TODO: finish
			return false;
		}
	}
}

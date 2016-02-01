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
	 * If you have a set of matching rules A and B, <code>A.implies(B)</code> will return true IFF B implies A.
	 * Therefore, if <code>A == B</code>, A implies A, so <code>A.implies(A) == true</code>.
	 * <br/>
	 * If you're not sure, just return false.
	 * @param otherRule
	 * @return
	 */
	default boolean implies(UserAgentMatchingRule otherRule) {
		return this.equals(otherRule);
	}
	
	/**
	 * Whether this rule is logically the same as the other rule
	 * @param otherRule
	 * @return
	 */
	default boolean equals(UserAgentMatchingRule otherRule) {
		return this == otherRule;
	}
	
	@Override
	public boolean test(ParsedUAToken[] tokens);
	
	default UserAgentMatchingRule andThen(UserAgentMatchingRule otherRule) {
		return new UAMatchesAllRule(this, otherRule);
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
		public boolean equals(UserAgentMatchingRule otherRule) {
			if (otherRule == this)
				return true;
			if (otherRule instanceof UAProductNameMatchingRule) {
				UAProductNameMatchingRule uapnmr = (UAProductNameMatchingRule) otherRule;
				return uapnmr.tokenNumber == this.tokenNumber && this.name.equals(uapnmr.name);
			}
			return false;
		}
		@Override
		public UserAgentMatchingRule andThen(UserAgentMatchingRule other) {
			if (other == this)
				return this;
			if (other instanceof UAProductVersionMatchingRule && ((UAProductVersionMatchingRule)other).tokenNumber == this.tokenNumber)
				return new UAProductMatchingRule(this.tokenNumber, this.name, ((UAProductVersionMatchingRule) other).version);
			return new UAMatchesAllRule(this, other);
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
		public boolean equals(UserAgentMatchingRule otherRule) {
			if (otherRule == this)
				return true;
			if (otherRule instanceof UAProductVersionMatchingRule) {
				UAProductVersionMatchingRule other = (UAProductVersionMatchingRule) otherRule;
				return other.tokenNumber == this.tokenNumber && this.version.equals(other.version);
			}
			return false;
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
		public boolean implies(UserAgentMatchingRule otherRule) {
			if (this.equals(otherRule))
				return true;
			if (otherRule instanceof UAProductNameMatchingRule) {
				UAProductNameMatchingRule other = (UAProductNameMatchingRule) otherRule;
				return other.tokenNumber == this.tokenNumber && this.name.equals(other.name);
			}
			if (otherRule instanceof UAProductVersionMatchingRule) {
				UAProductVersionMatchingRule other = (UAProductVersionMatchingRule) otherRule;
				return other.tokenNumber == this.tokenNumber && this.version.equals(other.version);
			}
			return false;
		}
		@Override
		public boolean equals(UserAgentMatchingRule otherRule) {
			if (otherRule instanceof UAProductMatchingRule) {
				UAProductMatchingRule other = (UAProductMatchingRule) otherRule;
				return other.tokenNumber == this.tokenNumber && this.name.equals(other.name) && this.version.equals(other.version);
			}
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
		public boolean equals(UserAgentMatchingRule otherRule) {
			if (otherRule == this)
				return true;
			if (otherRule instanceof UADetailMatchingRule) {
				UADetailMatchingRule other = (UADetailMatchingRule) otherRule;
				return other.token == this.token && this.detail == other.detail && this.value.equals(other.value);
			}
			return false;
		}
	}
	public static class UAMatchesAnyRule implements UserAgentMatchingRule {
		protected final UserAgentMatchingRule[] rules;
		public UAMatchesAnyRule(UserAgentMatchingRule...rules) {
			this.rules = rules;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			for (UserAgentMatchingRule rule : rules)
				if (rule.test(tokens))
					return true;
			return false;
		}
		@Override
		public boolean equals(UserAgentMatchingRule otherRule) {
			return otherRule == this;//TODO finish
		}
	}
	public static class UAMatchesAllRule implements UserAgentMatchingRule {
		protected final UserAgentMatchingRule[] rules;
		public UAMatchesAllRule(UserAgentMatchingRule...rules) {
			this.rules = rules;
		}
		@Override
		public boolean test(ParsedUAToken[] tokens) {
			for (UserAgentMatchingRule rule : rules)
				if (!rule.test(tokens))
					return false;
			return true;
		}
		@Override
		public boolean implies(UserAgentMatchingRule otherRule) {
			if (this.equals(otherRule))//TODO add better equals(UserAgentMatchingRule) impl.
				return true;
			for (UserAgentMatchingRule rule : rules)
				if (rule.implies(otherRule))
					return true;
			if (otherRule instanceof UAMatchesAnyRule) {
				UAMatchesAnyRule anyOther = (UAMatchesAnyRule) otherRule;
				for (UserAgentMatchingRule rule : rules)
					for (UserAgentMatchingRule anyOtherRule : anyOther.rules)
						if (rule.implies(anyOtherRule))
							return true;
			}
			return false;
		}
		@Override
		public UAMatchesAllRule andThen(UserAgentMatchingRule otherRule) {
			UserAgentMatchingRule[] allRules;
			if (otherRule instanceof UAMatchesAllRule) {
				UAMatchesAllRule other = (UAMatchesAllRule) otherRule;
				allRules = new UserAgentMatchingRule[this.rules.length + other.rules.length];
				System.arraycopy(other.rules, 0, allRules, this.rules.length, other.rules.length);
			} else {
				allRules = new UserAgentMatchingRule[this.rules.length + 1];
				allRules[this.rules.length] = otherRule;
			}
			System.arraycopy(this.rules, 0, allRules, 0, this.rules.length);
			return new UAMatchesAllRule(allRules);
		}
	}
}

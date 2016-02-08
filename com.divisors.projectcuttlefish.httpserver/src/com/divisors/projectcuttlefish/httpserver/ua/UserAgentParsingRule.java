package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.error.ParseException;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUATokenField;
import com.divisors.projectcuttlefish.httpserver.util.Constants;

/**
 * Specifies a rule to apply to a tokenized User-Agent string, to better extract
 * data from it.
 * @author mailmindlin
 *
 */
@FunctionalInterface
public interface UserAgentParsingRule extends BiConsumer<ParsedUAToken[], Map<String, String>> {
	@SuppressWarnings("unchecked")
	public static UserAgentParsingRule compileJSON(JSONObject json) {
		if (json.has("regex")) {
			int token = json.getInt("token");
			ParsedUATokenField target = ParsedUATokenField.valueOf(json.getString("target").toUpperCase());
			Pattern pattern = Pattern.compile(json.getString("regex"));
			
			//parse groups
			if (!json.has("groups"))
				return new UserAgentNOPParsingRule();//if no groups, then there is no point to this.
			JSONObject groups = json.getJSONObject("groups");
			HashMap<String, String> groupNameMappings = new HashMap<>();
			//TODO add value transformer support
			for (String group : groups.keySet()) {
				String key;
				JSONObject groupInfo = groups.optJSONObject(group);
				if (groupInfo != null) {
					//TODO add value transformer support
					key = groupInfo.getString("key");
				} else {
					key = groups.getString(group);
				}
				groupNameMappings.put(group, key);
			}
			return new UARegexParsingRule(token, pattern, target, groupNameMappings, Collections.EMPTY_MAP);
		}else if (json.has("token")) {
			int token = json.getInt("token");
			String key = json.getString("key");
			ParsedUATokenField field = ParsedUATokenField.valueOf(json.getString("target").toUpperCase());
			return new UASimpleParsingRule(token, key, field);
		} else if (json.has("key") && json.has("value")) {
			String key = json.getString("key");
			String value = json.getString("value");
			return new UAConstantRule(key, value);
		}
		return null;
	}
	@Override
	void accept(final ParsedUAToken[] tokens, Map<String, String> result);
	default UserAgentParsingRule andThen(UserAgentParsingRule then) {
		return (tokens, result) -> {
			this.accept(tokens, result);
			then.accept(tokens, result);
		};
	}
	/**
	 * No-op implementation.
	 * @author mailmindlin
	 */
	public static class UserAgentNOPParsingRule implements UserAgentParsingRule {
		@Override
		public void accept(ParsedUAToken[] tokens, Map<String, String> result) {
		}
		@Override
		public UserAgentParsingRule andThen(UserAgentParsingRule then) {
			return then;
		}
	}
	public static class UASimpleParsingRule implements UserAgentParsingRule {
		protected final int token;
		protected final String key;
		protected final ParsedUATokenField field;
		public UASimpleParsingRule(int token, String key, ParsedUATokenField field) {
			this.token = token;
			this.key = key;
			this.field = field;
			System.out.println("\tCreated simple rule: {token: " + token + ", key: '" + key + "', field: " + field + "}");
		}
		@Override
		public void accept(ParsedUAToken[] tokens, Map<String, String> result) {
			if (tokens.length <= token)
				throw new ParseException("Not enough tokens");
			result.put(key, tokens[token].getField(field));
		}
	}
	public static class UAConstantRule implements UserAgentParsingRule {
		protected final String key;
		protected final String value;
		public UAConstantRule(String key, String value) {
			this.key = key;
			this.value = value;
			System.out.println("\tCreated constant rule: {key: " + key + ", value: '" + value + "}");
		}
		@Override
		public void accept(ParsedUAToken[] tokens, Map<String, String> result) {
			result.put(key, value);
		}
	}
	public static class UARegexParsingRule implements UserAgentParsingRule {
		protected final int token;
		protected final Pattern pattern;
		protected final ParsedUATokenField field;
		protected final Map<String, String> groupNameMappings;
		protected final Map<String, Function<String, String>> valueTransformers;
		public UARegexParsingRule(int token, Pattern pattern, ParsedUATokenField field, Map<String, String> groupNameMappings, Map<String, Function<String,String>> valueTransformers) {
			this.token = token;
			this.pattern = pattern;
			this.field = field;
			this.groupNameMappings = groupNameMappings;
			this.valueTransformers = valueTransformers;
			System.out.println("\tCreated regex rule: {token: " + token + ", field: " + field + ", pattern: '" + pattern + "'}");
		}
		@Override
		public void accept(ParsedUAToken[] tokens, Map<String, String> result) {
			if (tokens.length <= token)
				throw new ParseException("Not enough tokens");
			String target = tokens[token].getField(field);
			Matcher m = pattern.matcher(target);
			if (!m.find())
				throw new ParseException("Unable to parse string '" + target + "' with pattern '" + pattern + "'");
			for (Entry<String, String> groupNameMapping : groupNameMappings.entrySet()) {
				String value = m.group(groupNameMapping.getKey());
				
				//Apply transformer. If no transformer is stored, it will use the identity transformer.
				@SuppressWarnings("unchecked")
				Function<String, String> transformer = valueTransformers.getOrDefault(groupNameMapping.getKey(), (Function<String, String>)Constants.IDENTITY_FN);
				value = transformer.apply(value);
				
				result.put(groupNameMapping.getValue(), value);
			}
		}
	}
}

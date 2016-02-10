package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.error.ParseException;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;

public class UABrowser implements Predicate<ParsedUAToken[]> {
	protected String name;
	protected String mfr;
	List<UserAgentMatchingRule> matchers;
	List<UserAgentParsingRule> parsers;
	@SuppressWarnings("unchecked")
	public UABrowser(JSONObject browserData) {
		this.name = browserData.getString("name");
		this.mfr = browserData.optString("mfr", "");
		
		//compile rules
		JSONArray ruleDefs = browserData.getJSONArray("patternRules");
		matchers = new ArrayList<>(ruleDefs.length());
		for (int i=0; i<ruleDefs.length(); i++)
			matchers.add(UserAgentMatchingRule.compileJSON(ruleDefs.getJSONObject(i)));
		//TODO: simlpify superseeding rules. Maybe make optional, because O(n!) time
		
		JSONArray parseDefs = browserData.optJSONArray("parseRules");
		if (parseDefs != null) {
			parsers = new ArrayList<>();
			for (int i=0; i<parseDefs.length(); i++) {
				try {
					parsers.add(UserAgentParsingRule.compileJSON(parseDefs.getJSONObject(i)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			parsers.removeIf(rule -> (rule == null));
		} else {
			this.parsers = Collections.EMPTY_LIST;
		}
	}
	public UABrowser(String name, String mfr, List<UserAgentMatchingRule> matchers) {
		this.name = name;
		this.mfr = mfr;
		this.matchers = matchers;//TODO: simplify superseeding rules
	}
	public String getName() {
		return name;
	}
	public String getManufacturer() {
		return mfr;
	}
	@Override
	public boolean test(ParsedUAToken[] tokens) {
		for (UserAgentMatchingRule rule : matchers)
			if (!rule.test(tokens))
				return false;
		return true;
	}
	public void parse(ParsedUAToken[] tokens, Map<String, String> result) {
		for (UserAgentParsingRule parser : parsers)
			parser.accept(tokens, result);
	}
}

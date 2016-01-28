package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;

public class UABrowser {
	protected String name;
	protected String mfr;
	List<UserAgentMatchingRule> rules;
	public UABrowser(JSONObject browserData) {
		this.name = browserData.getString("name");
		this.mfr = browserData.optString("mfr", "");
		
		//compile rules
		JSONArray ruleDefs = browserData.getJSONArray("patternRules");
		rules = new ArrayList<>(ruleDefs.length());
		for (int i=0; i<ruleDefs.length(); i++)
			rules.add(UserAgentMatchingRule.compileJSON(ruleDefs.getJSONObject(i)));
		//TODO: simlpify superseeding rules
	}
	public UABrowser(String name, String mfr, List<UserAgentMatchingRule> rules) {
		this.name = name;
		this.mfr = mfr;
		this.rules = rules;//TODO: simplify superseeding rules
	}
	public String getName() {
		return name;
	}
	public String getManufacturer() {
		return mfr;
	}
	public boolean test(ParsedUAToken[] tokens) {
		for (UserAgentMatchingRule rule : rules)
			if (!rule.test(tokens))
				return false;
		return true;
	}
}

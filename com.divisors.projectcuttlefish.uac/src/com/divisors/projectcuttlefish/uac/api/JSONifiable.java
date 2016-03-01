package com.divisors.projectcuttlefish.uac.api;

import org.json.JSONObject;

public interface JSONifiable {
	void writeJSON(JSONObject json);
	void readJSON(JSONObject json);
	default JSONObject toJSON() {
		JSONObject result = new JSONObject();
		writeJSON(result);
		return result;
	}
}

package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.util.Optional;

import org.json.JSONObject;

public class JSONRpcResult {
	protected final JSONObject json;
	public JSONRpcResult(JSONObject json) {
		this.json = json;
	}
	
	public JSONObject getJSON() {
		return json;
	}
	
	public String getVersion() {
		return getJSON().optString("jsonrpc");
	}
	public int getID() {
		return getJSON().getInt("id");
	}
	
	public Optional<JSONRpcException> getError() {
		JSONObject errorObj = getJSON().optJSONObject("error");
		if (errorObj == null)
			return Optional.empty();
		return Optional.of((JSONRpcException)getJSON().get("error"));
	}
}

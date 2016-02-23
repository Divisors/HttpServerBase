package com.divisors.projectcuttlefish.httpserver.api.rpc;

import org.json.JSONObject;

public class JSONRpcRequest {
	JSONObject json;
	public JSONRpcRequest() {
		this(new JSONObject());
	}
	public JSONRpcRequest(String methodName) {
		this();
		json.put("method", methodName);
		json.put("params", new JSONObject());
	}
	public JSONRpcRequest(JSONObject json) {
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
	
	public String getMethodName() {
		return getJSON().getString("method");
	}
	
	public JSONObject getParams() {
		return getJSON().getJSONObject("params");
	}
}

package com.divisors.projectcuttlefish.httpserver.api.rpc;

import org.json.JSONObject;

public class JSONRpcRequest {
	JSONObject json;
	public JSONRpcRequest(JSONObject json) {
		this.json = json;
	}
}

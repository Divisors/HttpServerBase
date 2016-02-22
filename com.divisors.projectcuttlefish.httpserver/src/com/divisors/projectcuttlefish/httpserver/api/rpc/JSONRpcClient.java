package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.util.Map;
import java.util.concurrent.Future;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

public abstract class JSONRpcClient {
	
	protected abstract Future<HttpResponse> doHttp(HttpRequest request);
	
	public abstract JSONRemote get(String url);
	public Future<JSONRpcResult> invoke(JSONRemote object, String methodName, Map<String, Object> paramsS) {
		return null;
	}
}

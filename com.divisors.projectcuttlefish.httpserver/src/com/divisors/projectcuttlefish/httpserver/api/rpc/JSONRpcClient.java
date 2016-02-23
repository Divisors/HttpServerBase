package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

public abstract class JSONRpcClient {
	
	protected Map<Class<? extends JSONRemote>, Map<String, MethodInfo>> prototypes = new ConcurrentHashMap<>();
	
	protected abstract Future<HttpResponse> doHttp(HttpRequest request);
	
	public abstract JSONRemote get(String url);
	
	protected Map<String, MethodInfo> buildPrototype(Class<? extends JSONRemote> clazz) {
		return null;
	}
	
	public Future<JSONRpcResult> invoke(JSONRemote object, String methodName, Map<String, Object> params) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException{
		Map<String, MethodInfo> prototype = prototypes.computeIfAbsent(object.getClass(), this::buildPrototype);
		
		MethodInfo method = prototype.get(methodName);
		
		if (method == null)
			throw new NoSuchMethodException("Unknown method " + object.getClass() + '#' + methodName);
		
		if (method.accessibility != JSONRpcAccessibility.PUBLIC)
			throw new IllegalAccessException("Illegal invocation of " + object.getClass() + '#' + methodName);
		
		//test if I can invoke it
		Set<String> paramNames = params.keySet();
		if (!paramNames.equals(method.params.keySet())) {
			if (!method.params.keySet().containsAll(paramNames)) {
				paramNames = new HashSet<>(paramNames);//copy set
				paramNames.removeAll(method.params.keySet());
				throw new IllegalArgumentException("Unknown parameters: " + paramNames);
			}
			
			if (!paramNames.containsAll(method.params.keySet())) {
				Set<String> missingParams = new HashSet<>(method.params.keySet());//copy set
				missingParams.removeAll(paramNames);
				throw new IllegalArgumentException("Missing parameters: " + missingParams);
			}
		}
		
		return null;
	}
	
	protected class MethodInfo {
		final Method method;
		final String name;
		final Map<String, JSONParameterType> params;
		final JSONRpcAccessibility accessibility;
		MethodInfo(Method method, String name, Map<String, JSONParameterType> params, JSONRpcAccessibility accessibility) {
			this.method = method;
			this.name = name;
			this.params = params;
			this.accessibility = accessibility;
		}
	}
}

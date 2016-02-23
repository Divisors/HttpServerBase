package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public abstract class JSONRpcClient {
	static final ClassPool pool = ClassPool.getDefault();
	static final CtClass JSON_RPC_CLIENT_CLASS;
	static final CtClass GENERATED_IMPL_CLASS;
	static final CtClass JSON_REMOTE_INTERFACE;
	static final CtClass STRING_CLASS;
	static {
		JSON_RPC_CLIENT_CLASS = loadCt(JSONRpcClient.class.getCanonicalName());
		GENERATED_IMPL_CLASS = loadCt(JSONRpcClient.class.getCanonicalName() + ".JSONRpcGeneratedImplementation");
		JSON_REMOTE_INTERFACE = loadCt(JSONRemote.class.getCanonicalName());
		STRING_CLASS = loadCt(S.class.getCanonicalName());
	}
	
	private static CtClass loadCt(String name) {
		try {
			return pool.get(name);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Map<Class<? extends JSONRemote>, Map<String, MethodInfo>> metadata = new ConcurrentHashMap<>();
	protected Map<Class<? extends JSONRemote>, CtClass> proto = new ConcurrentHashMap<>();
	
	protected abstract Future<HttpResponse> doHttp(HttpRequest request);
	
	public <E extends JSONRemote> E get(String url, Class<E> clazz) {
		//TODO finish
		@SuppressWarnings("unchecked")
		Class<E> prototype = pool.toClass(proto.get(clazz));
		
		E result;
		
		try {
			result = prototype.getDeclaredConstructor(getClass(), String.class).newInstance(this, url);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	protected Map<String, MethodInfo> getMeta(Class<? extends JSONRemote> clazz) {
		
		return null;
	}
	
	protected Class<? extends JSONRemote> generateProto(Class<? extends JSONRemote> clazz) {
		Map<String, MethodInfo> meta = metadata.computeIfAbsent(clazz, this::getMeta);
		if (clazz.isInterface()) {
			CtClass impl = pool.makeClass(clazz.getCanonicalName() + "__RPC", GENERATED_IMPL_CLASS);
			impl.addInterface(JSON_REMOTE_INTERFACE);
			impl.addInterface(pool.get(clazz.getCanonicalName()));
		}
	}
	
	public Future<JSONRpcResult> invoke(JSONRemote object, String methodName, Map<String, Object> params) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException{
		Map<String, MethodInfo> prototype = meta.computeIfAbsent(object.getClass(), this::getMeta);
		
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
	protected abstract class JSONRpcGeneratedImplementation implements JSONRemote {
		public JSONRpcGeneratedImplementation(JSONRpcClient client, String url) {
			
		}
	}
}

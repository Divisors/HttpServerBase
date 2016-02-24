package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Function;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public abstract class JSONRpcClient {
	static final ClassPool pool = ClassPool.getDefault();
	static final CtClass JSON_RPC_CLIENT_CLASS;
	static final CtClass RPC_IMPL_CLASS;
	static final CtClass GENERATED_IMPL_CLASS;
	static final CtClass JSON_REMOTE_INTERFACE;
	static final CtClass STRING_CLASS;
	static {
		RPC_IMPL_CLASS = loadCt(JSONRpcClient.JSONRpcImplementation.class.getName());
		JSON_RPC_CLIENT_CLASS = loadCt(JSONRpcClient.class.getName());
		GENERATED_IMPL_CLASS = loadCt(JSONRpcClient.JSONRpcGeneratedImplementation.class.getName());
		JSON_REMOTE_INTERFACE = loadCt(JSONRemote.class.getName());
		STRING_CLASS = loadCt(String.class.getName());
	}
	
	private static CtClass loadCt(String name) {
		try {
			return pool.get(name);
		} catch (NotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected Map<Class<? extends JSONRemote>, ClassInfo> metadata = new ConcurrentHashMap<>();
	protected Map<Class<? extends JSONRemote>, Class<? extends JSONRemote>> proto = new ConcurrentHashMap<>();
	
	protected abstract Future<HttpResponse> doHttp(HttpRequest request);
	
	public <E extends JSONRemote> E get(String url, Class<E> clazz) {
		//TODO finish
		@SuppressWarnings("unchecked")
		Class<E> prototype = (Class<E>) proto.get(clazz);
		
		E result;
		
		try {
			result = prototype.getDeclaredConstructor(getClass(), String.class).newInstance(this, url);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	protected ClassInfo getMeta(Class<? extends JSONRemote> clazz) {
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	protected Class<? extends JSONRemote> generateProto(Class<? extends JSONRemote> clazz) throws NotFoundException, CannotCompileException {
		CtClass result;
		String className = clazz.getName() + "__RPC";
		if (clazz.isInterface()) {
			result = pool.makeClass(className, GENERATED_IMPL_CLASS);
			result.addInterface(JSON_REMOTE_INTERFACE);
			result.addInterface(pool.get(clazz.getName()));
			
			//Create constructor to invoke 
			CtConstructor constructor = CtNewConstructor.make(new CtClass[]{JSON_RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, result);
			constructor.setModifiers(Modifier.PUBLIC);
			result.addConstructor(constructor);
		} else {
			//is abstract class JSONRpcGeneratedImplementation(JSONRpcClient, String)
			result = pool.makeClass(className, pool.get(clazz.getName()));
			result.addInterface(RPC_IMPL_CLASS);
			
			CtField rpcClientField = new CtField(JSON_RPC_CLIENT_CLASS, "__rpcClient", result);
			rpcClientField.setModifiers(Modifier.PRIVATE | Modifier.FINAL | Modifier.TRANSIENT);
			result.addField(rpcClientField);
			
			CtField rpcUrlField = new CtField(STRING_CLASS, "__rpcUrl", result);
			rpcUrlField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
			result.addField(rpcUrlField);
			
			//Create constructor to invoke
			CtConstructor constructor = CtNewConstructor.make(
				"public " + className + '(' + getClass().getName() + " client, java.lang.String url) {"
					+ "super();"
					+ "this." + rpcClientField.getName() + " = client;"
					+ "this." + rpcUrlField.getName() + " = url;"
				+ "}", result);
			result.addConstructor(constructor);
			
			//implement JSONRpcImplementation
			CtMethod getClientMethod = CtNewMethod.getter("getClient", rpcClientField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getClientMethod);
			
			CtMethod getUrlMethod = CtNewMethod.getter("getRpcUrl", rpcUrlField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getUrlMethod);
		}
		
		//now generate methods
		ClassInfo classInfo = metadata.computeIfAbsent(clazz, this::getMeta);
		for (Entry<String, MethodInfo> methodData : classInfo.methods.entrySet()) {
			MethodInfo methodInfo = methodData.getValue();
			Method method = methodInfo.method;
			
			//Start with 'public returnType methodName('
			StringBuilder methodText = new StringBuilder("public ")
					.append(method.getName())
					.append('(');
			//build param string
			for (Map.Entry<String, JSONParameterType> param : methodInfo.params.entrySet()) {
				
			}
			
			CtMethod generatedOverride = CtNewMethod.m(methodText.toString(), result, "__rpcClient", "doHttp");
			generatedOverride.setModifiers(method.getModifiers());
			result.addMethod(generatedOverride);
		}
		
		return result.toClass();
	}
	
	public Future<JSONRpcResult> invoke(JSONRemote object, String methodName, Map<String, Object> params) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException{
		ClassInfo classInfo = metadata.computeIfAbsent(object.getClass(), this::getMeta);
		
		MethodInfo method = classInfo.methods.get(methodName);
		
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
	
	protected class ClassInfo {
		Class<? extends JSONRemote> clazz;
		Map<String, MethodInfo> methods;
	}
	
	protected class MethodInfo {
		final Method method;
		final String name;
		final Map<String, JSONParameterType> params;
		final JSONRpcAccessibility accessibility;
		Function<Object[], JSONRpcRequest> preparser;
		Function<JSONRpcResult, Object> postparser;
		MethodInfo(Method method, String name, Map<String, JSONParameterType> params, JSONRpcAccessibility accessibility) {
			this.method = method;
			this.name = name;
			this.params = params;
			this.accessibility = accessibility;
		}
	}
	protected interface JSONRpcImplementation extends JSONRemote {
		JSONRpcClient getClient();
		String getRpcUrl();
	}
	protected abstract class JSONRpcGeneratedImplementation implements JSONRpcImplementation {
		protected final JSONRpcClient __rpcClient;
		protected final String __rpcUrl;
		public JSONRpcGeneratedImplementation(JSONRpcClient client, String url) {
			this.__rpcClient = client;
			this.__rpcUrl = url;
		}
		
		@Override
		public JSONRpcClient getClient() {
			return __rpcClient;
		}
		
		@Override
		public String getRpcUrl() {
			return __rpcUrl;
		}
		
	}
}

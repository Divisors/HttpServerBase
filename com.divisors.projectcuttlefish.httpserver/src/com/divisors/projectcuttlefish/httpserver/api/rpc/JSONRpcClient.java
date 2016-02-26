package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.sun.org.apache.bcel.internal.classfile.Field;

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
	
	private static CtClass getCt(String className) throws NotFoundException {
		return pool.get(className);
	}
	private static CtClass getCt(Class<?> clazz) throws NotFoundException {
		return pool.get(clazz.getName());
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
		CtClass clazzCt = getCt(clazz);
		String outClassName = clazz.getName() + "__RPC";
		
		if (clazz.isInterface()) {
			result = pool.makeClass(outClassName, GENERATED_IMPL_CLASS);
			result.addInterface(JSON_REMOTE_INTERFACE);
			result.addInterface(pool.get(clazz.getName()));
			
			//Create constructor to invoke 
			CtConstructor constructor = CtNewConstructor.make(new CtClass[]{JSON_RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, result);
			constructor.setModifiers(Modifier.PUBLIC);
			result.addConstructor(constructor);
		} else {
			//is abstract class JSONRpcGeneratedImplementation(JSONRpcClient, String)
			result = pool.makeClass(outClassName, pool.get(clazz.getName()));
			result.addInterface(RPC_IMPL_CLASS);
			
			CtField rpcClientField = new CtField(JSON_RPC_CLIENT_CLASS, "__rpcClient", result);
			rpcClientField.setModifiers(Modifier.PRIVATE | Modifier.FINAL | Modifier.TRANSIENT);
			result.addField(rpcClientField);
			
			CtField rpcUrlField = new CtField(STRING_CLASS, "__rpcUrl", result);
			rpcUrlField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
			result.addField(rpcUrlField);
			
			//Create constructor to invoke
			result.addConstructor(CtNewConstructor.make(new CtClass[]{JSON_RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, "super();$0.__rpcClient = $1;$0.__rpcUrl=$2", result));
			
			//implement JSONRpcImplementation (generate getters)
			CtMethod getClientMethod = CtNewMethod.getter("getClient", rpcClientField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getClientMethod);
			
			CtMethod getUrlMethod = CtNewMethod.getter("getRpcUrl", rpcUrlField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getUrlMethod);
		}
		
		//now generate methods
		ClassInfo<?> classInfo = metadata.computeIfAbsent(clazz, this::getMeta);
		for (Entry<String, MethodInfo> methodData : classInfo.methods.entrySet()) {
			MethodInfo methodInfo = methodData.getValue();
			Method method = methodInfo.method;
			
			CtClass returnType = getCt(method.getReturnType());
			
			//build param array
			CtClass[] params = new CtClass[methodInfo.params.size()];
			int i = 0;
			for (ParamInfo param : methodInfo.params.values())
				params[i++] = getCt((Class<?>) param.type.getType());
			
			StringBuilder methodBody = new StringBuilder("{\n");
			String preparserSignature = methodInfo.annotation.preparser();
			String postparserSignature = methodInfo.annotation.postparser();
			if (preparserSignature != null && !preparserSignature.isEmpty()) {
				String preparserName = preparserSignature;
				String[] preparserArgs;
				CtClass ownerClass = clazzCt;
				
				int idxToken;
				if ((idxToken = preparserName.indexOf("#")) > 0) {
					ownerClass = getCt(preparserName.substring(0, idxToken));
					preparserName = preparserName.substring(idxToken);
				}
				
				if ((idxToken = preparserName.indexOf('(')) > 0) {
					String preparserArgsText = preparserName.substring(idxToken, '(');
					preparserName = preparserName.substring(0, idxToken);
				}
				//get preparser
				
				for (Method declared : clazz.getDeclaredMethods())
					if (declared.getName().equals(preparserName))
				methodBody.append("org.json.JSONObject params = $0");
				
			} else {
				//try to build JSONObject from params
				
			}
			
			CtMethod generatedOverride = CtNewMethod.make(method.getModifiers(), returnType, method.getName(), params, new CtClass[]{}, methodBody.toString(), result);
			result.addMethod(generatedOverride);
		}
		
		return result.toClass();
	}
	
	public Future<JSONRpcResult> invokeRemote(String url, String methodName, JSONObject params) {
		return null;//TODO fin
	}
	public Future<JSONRpcResult> invoke(JSONRemote object, String methodName, Map<String, Object> params) throws IllegalArgumentException, IllegalAccessException, NoSuchMethodException{
		ClassInfo<?> classInfo = metadata.computeIfAbsent(object.getClass(), this::getMeta);
		
		MethodInfo method = classInfo.methods.get(methodName);
		
		if (method == null)
			throw new NoSuchMethodException("Unknown method " + object.getClass() + '#' + methodName);
		
		if (method.accessibility != JSONRpcAccessibility.PUBLIC)
			throw new IllegalAccessException("Illegal invocation of " + object.getClass() + '#' + methodName);
		
		//test if I can invoke it
		Set<String> paramNames = params.keySet();
		//TODO finish
		/*if (!paramNames.equals(method.params)) {
			if (!method.params.containsAll(paramNames)) {
				paramNames = new HashSet<>(paramNames);//copy set
				paramNames.removeAll(method.params);
				throw new IllegalArgumentException("Unknown parameters: " + paramNames);
			}
			
			if (!paramNames.containsAll(method.params)) {
				Set<String> missingParams = new HashSet<>(method.params);//copy set
				missingParams.removeAll(paramNames);
				throw new IllegalArgumentException("Missing parameters: " + missingParams);
			}
		}*/
		
		return null;
	}
	
	protected class ClassInfo<E extends JSONRemote> {
		/**
		 * Class that this is wrapping
		 */
		Class<E> clazz;
		/**
		 * Generated class reference
		 */
		Class<? extends E> impl;
		/**
		 * Methods that were annotated
		 */
		Map<String, MethodInfo> methods;
		/**
		 * Function wrapping generated constructor
		 */
		BiFunction<? extends JSONRpcClient, String, E> construct;
	}
	
	/**
	 * Information about wrapped method
	 * @author mailmindlin
	 */
	protected class MethodInfo {
		/**
		 * Method that this is wrapping
		 */
		final Method method;
		/**
		 * Extern annotation on this method
		 */
		final JSONRpcExtern annotation;
		/**
		 * External name for this method
		 */
		final String name;
		/**
		 * Parameters
		 */
		final Map<String, ParamInfo> params;
		/**
		 * JSONRpc accessibility (i.e., can it be only accessed by trusted clients)
		 */
		final JSONRpcAccessibility accessibility;
		MethodInfo(Method method, String name, Map<String, ParamInfo> params, JSONRpcExtern annotation) {
			this.method = method;
			this.name = name;
			this.params = params;
			this.annotation = annotation;
			this.accessibility = annotation.accessibility();
		}
	}
	
	protected class ParamInfo {
		AnnotatedType type;
		JSONRpcParam annotation;
		JSONParameterType jsonType;
		String name;
		String defaultValue;
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

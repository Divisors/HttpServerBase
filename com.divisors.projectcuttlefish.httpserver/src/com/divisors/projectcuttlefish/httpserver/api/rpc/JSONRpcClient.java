package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

import org.json.JSONObject;

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
	
	private static CtClass getCt(String className) throws NotFoundException {
		return pool.get(className);
	}
	private static CtClass getCt(Class<?> clazz) throws NotFoundException {
		return pool.get(clazz.getName());
	}
	
	/**
	 * Get the class of the primitive type boxed. For example, if this method is called with <code>int.class</code>, it will return <code>Integer.class</code>
	 * @param primitive the type to box
	 * @return the boxed class, or the input if the input is not primitive
	 * @see #unbox(Class)
	 * @see #boxArray(Class)
	 */
	public static Class<?> box(Class<?> primitive) {
		if (primitive == boolean.class)
			return Boolean.class;
		if (primitive == byte.class)
			return Byte.class;
		if (primitive == char.class)
			return Character.class;
		if (primitive == short.class)
			return Short.class;
		if (primitive == int.class)
			return Integer.class;
		if (primitive == long.class)
			return Long.class;
		if (primitive == float.class)
			return Float.class;
		if (primitive == double.class)
			return Double.class;
		return primitive;
	}
	/**
	 * Get the type of the given type unboxed. For example, if this method is called with <code>Integer.class</code>, it will return <code>int.class</code>
	 * @param boxed type to unbox
	 * @return the primitive type, or the input type if it's not a boxed type
	 * @see #box(Class)
	 * @see #unboxArray(Class)
	 */
	public static Class<?> unbox(Class<?> boxed) {
		if (boxed == Boolean.class)
			return boolean.class;
		if (boxed == Byte.class)
			return byte.class;
		if (boxed == Character.class)
			return char.class;
		if (boxed == Short.class)
			return short.class;
		if (boxed == Integer.class)
			return int.class;
		if (boxed == Long.class)
			return long.class;
		if (boxed == Float.class)
			return float.class;
		if (boxed == Double.class)
			return double.class;
		return boxed;
	}
	public static Class<?> getArrayClass(Class<?> componentType) throws ClassNotFoundException {
	    ClassLoader classLoader = componentType.getClassLoader();
	    String name;
	    if(componentType.isArray()){
	        // just add a leading "["
	        name = "["+componentType.getName();
	    }else if(componentType == boolean.class){
	        name = "[Z";
	    }else if(componentType == byte.class){
	        name = "[B";
	    }else if(componentType == char.class){
	        name = "[C";
	    }else if(componentType == double.class){
	        name = "[D";
	    }else if(componentType == float.class){
	        name = "[F";
	    }else if(componentType == int.class){
	        name = "[I";
	    }else if(componentType == long.class){
	        name = "[J";
	    }else if(componentType == short.class){
	        name = "[S";
	    }else{
	        // must be an object non-array class
	        name = "[L"+componentType.getName()+";";
	    }
	    return classLoader != null ? classLoader.loadClass(name) : Class.forName(name);
	}
	public static Class<?> getArrayClass(Class<?> componentType, int levels) throws ClassNotFoundException {
	    ClassLoader classLoader = componentType.getClassLoader();
	    String name;
	    if(componentType.isArray()){
	        // just add a leading "["
	        name = "["+componentType.getName();
	    }else if(componentType == boolean.class){
	        name = "[Z";
	    }else if(componentType == byte.class){
	        name = "[B";
	    }else if(componentType == char.class){
	        name = "[C";
	    }else if(componentType == double.class){
	        name = "[D";
	    }else if(componentType == float.class){
	        name = "[F";
	    }else if(componentType == int.class){
	        name = "[I";
	    }else if(componentType == long.class){
	        name = "[J";
	    }else if(componentType == short.class){
	        name = "[S";
	    }else{
	        // must be an object non-array class
	        name = "[L"+componentType.getName()+";";
	    }
	    //TODO make more efficient
	    for (levels--; levels > 0; levels--)
	    	name = "[" + name;
	    return classLoader != null ? classLoader.loadClass(name) : Class.forName(name);
	}
	/**
	 * Convert an
	 * @param primitiveArray
	 * @return
	 * @see #unbox(Class)
	 * @see #boxArray(Class)
	 */
	public static Class<?> unboxArray(Class<?> primitiveArray) {
		if (!primitiveArray.isArray())
			return primitiveArray;
		Class<?> primitive = primitiveArray;
		if (primitive.isArray()) {
			int levels;
			for (levels = 1; primitive.isArray(); levels++)
				primitive = primitive.getComponentType();
			try {
				//loop for multidimensional arrays
				return getArrayClass(unbox(primitive), levels);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		
		if (!primitive.isPrimitive())
			return primitiveArray;
		try {
			return getArrayClass(unbox(primitive));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return primitiveArray;
		}
	}
	/**
	 * Convert a primitive array to an array of the boxed type of that primitive. Multidimensional arrays supported.
	 * <br/>
	 * TODO maybe merge with {@link #box(Class)}
	 * @param primitiveArray
	 * @return
	 * @see #box(Class)
	 * @see #unboxArray(Class)
	 */
	public static Class<?> boxArray(Class<?> array) {
		if (!array.isArray())
			return array;
		
		Class<?> component = array;
		if (component.isArray()) {
			int levels;
			for (levels = 1; component.isArray(); levels++)
				component = component.getComponentType();
			try {
				//loop for multidimensional arrays
				return getArrayClass(box(component), levels);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
				return null;
			}
		}
		
		if (component.isPrimitive())
			return array;
		
		try {
			return getArrayClass(box(component));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return array;
		}
	}
	
	protected Map<Class<? extends JSONRemote>, ClassInfo<?>> metadata = new ConcurrentHashMap<>();
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
	
	protected <E extends JSONRemote> ClassInfo<E> getMeta(Class<E> clazz) {
		ClassInfo<E> result = new ClassInfo<>();
		result.clazz = clazz;
		
		result.methods = new HashMap<>();
		Set<Method> methods = new HashSet<>(Arrays.asList(clazz.getDeclaredMethods()));
		methods.addAll(Arrays.asList(clazz.getMethods()));
		methods.removeIf(method->(!method.isAnnotationPresent(JSONRpcExtern.class)));
		methods.forEach(method->{
			System.out.println("Pulling metadata from '" + method + "'");
			JSONRpcExtern methodAnnotation = method.getAnnotation(JSONRpcExtern.class);
			
			Map<String, ParamInfo> parameters = new HashMap<>();
			for (AnnotatedType param : method.getAnnotatedParameterTypes()) {
				System.out.println("Building info for " + param.getType() + Arrays.toString(param.getDeclaredAnnotations()));
				JSONRpcParam info = param.getDeclaredAnnotation(JSONRpcParam.class);
				System.out.println(info);
				ParamInfo parameter = new ParamInfo();
				parameter.annotation = info;
				parameter.defaultValue = info.defaultValue();
				parameter.jsonType = info.type();
				parameter.name = info.value();
				parameter.type = param;
				parameters.put(info.value(), parameter);
			}
			result.methods.put(methodAnnotation.name(), new MethodInfo(method, methodAnnotation.name(), parameters, methodAnnotation));
		});
		return result;
	}
	
	public static Set<Method> getMethodBySignature(Class<?> clazz, String methodName, String[] args, int maxArgs, Set<String> allowedExceptions) {
		Set<Method> candidates = new HashSet<>();
		
		for (Method declared : clazz.getDeclaredMethods())
			if (declared.getName().equals(methodName))
				candidates.add(declared);
		
		for (Method method : clazz.getMethods())
			if (method.getName().equals(methodName))
				candidates.add(method);
		
		if (args != null) {
			int numArgs = maxArgs > 0 ? Math.min(args.length, maxArgs) : args.length;
			candidates.removeIf(method->(method.getParameterCount() > numArgs));
			for (Method candidate : candidates) {
				int i = 0;
				for (Class<?> param : candidate.getParameterTypes()) {
					String arg = args[i++];
					
					if (param.getSimpleName().equals(arg) || param.getName().equals(arg))
						continue;
					
					if (param.isPrimitive()) {
						//try unboxing
						Class<?> boxed = box(param);
						if (boxed.getSimpleName().equals(arg) || boxed.getName().equals(arg))
							continue;
					} else {
						Class<?> unboxed = unbox(param);
						if (unboxed != param && (unboxed.getSimpleName().equals(arg) || unboxed.getName().equals(arg)))
							continue;
					}
					
					if (param.isArray()) {
						Class<?> unboxed = unboxArray(param);
						if (unboxed.getSimpleName().equals(arg) || unboxed.getName().equals(arg))
							continue;
						Class<?> boxed = boxArray(param);
						if (boxed.getSimpleName().equals(arg) || boxed.getName().equals(arg))
							continue;
					}
					candidates.remove(candidate);
					break;
				}
			}
		} else if (maxArgs > 0) {
			candidates.removeIf(method->(method.getParameterCount() > maxArgs));
		}
		
		for (Method candidate : candidates) {
			for (Class<?> exception : candidate.getExceptionTypes()) {
				if ((!allowedExceptions.contains(exception)) && !(exception.isInstance(RuntimeException.class))) {
					candidates.remove(candidate);
					break;
				}
			}
		}
		
		return candidates;
	}
	@SuppressWarnings("unchecked")
	public Class<? extends JSONRemote> generateProto(Class<? extends JSONRemote> clazz) throws NotFoundException, CannotCompileException {
		CtClass result;
		CtClass clazzCt = getCt(clazz);
		String outClassName = clazz.getName() + "__RPC";
		
		if (clazz.isInterface()) {
			System.out.println("Implementing " + clazz.getName() + " @ " + outClassName);
			result = pool.makeClass(outClassName, GENERATED_IMPL_CLASS);
			result.addInterface(JSON_REMOTE_INTERFACE);
			result.addInterface(pool.get(clazz.getName()));
			
			//Create constructor to invoke 
			CtConstructor constructor = CtNewConstructor.make(new CtClass[]{JSON_RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, result);
			constructor.setModifiers(Modifier.PUBLIC);
			result.addConstructor(constructor);
		} else {
			System.out.println("Extending " + clazz.getName() + " @ " + outClassName);
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
			result.addConstructor(CtNewConstructor.make(new CtClass[]{JSON_RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, "{super();$0.__rpcClient = $1;$0.__rpcUrl=$2;}", result));
			
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
			System.out.println("Implementing " + methodData.getKey());
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
				String[] preparserArgs = null;
				String[] preparserExceptions = null;
				CtClass ownerClass = clazzCt;
				
				int idxToken;
				if ((idxToken = preparserName.indexOf("#")) > 0 || (idxToken = preparserName.indexOf(".")) > 0) {
					ownerClass = getCt(preparserName.substring(0, idxToken));
					preparserName = preparserName.substring(idxToken);
				}
				
				if ((idxToken = preparserName.indexOf("throws")) > 0) {
					String exceptionsText = preparserName.substring(idxToken + 6).trim();
					preparserExceptions = exceptionsText.split(",");
					
					preparserName.substring(0, idxToken);
				}
				
				if ((idxToken = preparserName.indexOf('(')) > 0) {
					String preparserArgsText = preparserName.substring(idxToken, '(');
					preparserName = preparserName.substring(0, idxToken);
					
					if ((idxToken = preparserArgsText.indexOf(")")) > 0)
						preparserArgsText = preparserArgsText.substring(0, idxToken);
					
					preparserArgs = preparserArgsText.split(",");//TODO fix for generics
				}
				
				System.out.println("Preparser name: " + preparserName);
				System.out.println("Preparser args: " + Arrays.toString(preparserArgs));
				System.out.println("Preparser exceptions: " + Arrays.toString(preparserExceptions));
				System.out.println("Preparser owner: " + ownerClass);
				
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
//		Set<String> paramNames = params.keySet();
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

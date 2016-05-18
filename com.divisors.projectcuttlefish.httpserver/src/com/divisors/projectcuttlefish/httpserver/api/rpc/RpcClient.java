package com.divisors.projectcuttlefish.httpserver.api.rpc;

public class RpcClient {
	//TODO replace with cache
	protected final ConcurrentHashMap<Class<? extends RpcRemote>, CtClass> bindings = new ConcurrentHashMap<>();
	protected CtClass bind(Class<? extends RpcRemote> javaClass) {
		CtClass result;
		CtClass clazz = pool.get(javaClass);
		String outClassName = javaClass.getName() + "__RPC";
		
		if (javaClass.isAnonymousClass()) {
			throw new IllegalArgumentException("Anonymous classes (such as " + javaClass.getName() + ") cannot be bound");
		} else if (javaClass.isAnnotation()) {
			throw new IllegalArgumentException("Annotations (such as " + javaClass.getName() + ") cannot be remote");
		} else if (javaClass.isArray()) {
			throw new IllegalArgumentException("Arrays (such as " + javaClass.getName() + ") cannot be remote");
		} else if (javaClass.isMemberClass()) {
			throw new IllegalArgumentException("Member classes (such as " + javaClass.getName() + ") cannot be bound");
		} else if (javaClass.isPrimitive()) {
			throw new IllegalArgumentException("Primitive classes (such as " + javaClass.getName() + ") cannot be bound");
		} else if (javaClass.isEnum()) {
			throw new IllegalArgumentException("Enum (such as " + javaClass.getName() + ") cannot be bound");
		} else if (javaClass.getModifiers() & Modifier.FINAL == Modifier.FINAL) {
			throw new IllegalArgumentException("Final classes (such as " + javaClass.getName() + ") cannot be bound");
		} else if (javaClass.isInterface()) {
			System.out.println("Implementing " + javaClass.getName() + " -> " + outClassName);
			result = pool.makeClass(outClassName, RPC_BINDINGS_CLASS);
			result.addInterface(RPC_REMOTE_INTERFACE);
			result.addInterface(clazz);
			
			//Create constructor to invoke 
			CtConstructor constructor = CtNewConstructor.make(new CtClass[]{RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, result);
			constructor.setModifiers(Modifier.PUBLIC);
			result.addConstructor(constructor);
		} else {
			System.out.println("Extending " + javaClass.getName() + " -> " + outClassName);
			//is abstract class RpcGeneratedClient(RpcClient, String)
			result = pool.makeClass(outClassName, clazz);
			result.addInterface(RPC_IMPL_CLASS);
			
			CtField rpcClientField = new CtField(JSON_RPC_CLIENT_CLASS, "__rpcClient", result);
			rpcClientField.setModifiers(Modifier.PRIVATE | Modifier.FINAL | Modifier.TRANSIENT);
			result.addField(rpcClientField);
			
			CtField rpcNameField = new CtField(STRING_CLASS, "__rpcName", result);
			rpcNameField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
			result.addField(rpcNameField);
			
			//Create constructor to invoke
			//TODO support non-null constructor extending
			result.addConstructor(CtNewConstructor.make(new CtClass[]{RPC_CLIENT_CLASS, STRING_CLASS}, new CtClass[]{}, "{super();$0.__rpcClient=$1;$0.__rpcName=$2;}", result));
			
			//implement JSONRpcImplementation (generate getters)
			CtMethod getClientMethod = CtNewMethod.getter("__getRpcClient", rpcClientField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getClientMethod);
			
			CtMethod getUrlMethod = CtNewMethod.getter("__getRpcName", rpcNameField);
			getClientMethod.setModifiers(Modifier.PUBLIC | Modifier.FINAL);
			result.addMethod(getUrlMethod);
		}
	}
	protected CtClass getBindings(Class<?> clazz) {
		return bindings.computeIfAbsent(clazz, this::bind);
	}
	public <T extends RpcRemote> T getRemote(Class<? extends T> clazz, String name) {
		
	}
	protected class AbstractRpcRemoteBindings implements RpcRemote {
		
	}
}

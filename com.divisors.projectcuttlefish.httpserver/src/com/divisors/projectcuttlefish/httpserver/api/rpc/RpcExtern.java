package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RpcExtern {
	String name() default "";
	JSONRpcAccessibility accessibility() default JSONRpcAccessibility.SECURED;
	String preparser() default "";
	String postparser() default "";
}

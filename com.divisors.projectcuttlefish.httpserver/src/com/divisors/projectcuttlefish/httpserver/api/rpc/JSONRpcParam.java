package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface JSONRpcParam {
	String value();
	String defaultValue() default "";
	JSONParameterType type() default JSONParameterType.DETECT;
}

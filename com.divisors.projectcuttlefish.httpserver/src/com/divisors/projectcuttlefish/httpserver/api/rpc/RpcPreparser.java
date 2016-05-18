package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
//TODO make repeatable
public @interface RpcParameterSerializer {
	/**
	 * Method to serialize parameters for
	 */
	String value();
}

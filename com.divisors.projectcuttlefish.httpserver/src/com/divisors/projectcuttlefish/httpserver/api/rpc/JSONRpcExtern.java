package com.divisors.projectcuttlefish.httpserver.api.rpc;

public @interface JSONRpcExtern {
	JSONRpcAccessibility value() default JSONRpcAccessibility.SECURED;
}

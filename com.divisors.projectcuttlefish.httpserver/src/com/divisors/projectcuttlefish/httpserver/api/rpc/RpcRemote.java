package com.divisors.projectcuttlefish.httpserver.api.rpc;

import java.io.IOException;
import java.rmi.Remote;

public interface RpcRemote extends Remote {
	default <E> E doInvoke(String methodName, Object...args) throws IllegalArgumentException, IOException, RpcException {
		//TODO finish
		return null;
	}
}

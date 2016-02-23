package com.divisors.projectcuttlefish.crypto.api.random;

import java.util.concurrent.Future;

import org.json.JSONArray;

import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRemote;
import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRpcAccessibility;
import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRpcExtern;
import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRpcParam;
import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRpcResult;
import com.divisors.projectcuttlefish.httpserver.api.rpc.JSONRpcResultParser;

/**
 * RPC API for Random.org
 * @author mailmidnlin
 */
public abstract class RandomRpc implements JSONRemote {
	/**
	 * Generate random integers in range
	 * @param apiKey api key
	 * @param num number of integers to generate
	 * @param min minimum integer value. <code>-10^9 <= min <= 10^9</code>
	 * @param max maximum integer value. <code>-10^9 <= min <= 10^9</code>
	 * @param base base to generate numbers in.
	 * @param replace whether to generate with replacement. default true
	 * @return random integers
	 */
	@JSONRpcExtern(value = "generateIntegers", accessibility = JSONRpcAccessibility.PUBLIC)
	public abstract Future<int[]> generateIntegers(@JSONRpcParam("apiKey") String apiKey, @JSONRpcParam("n") int num, @JSONRpcParam("min") long min, @JSONRpcParam("max") long max, @JSONRpcParam(value="base", defaultValue="10") int base, @JSONRpcParam(value="replace", defaultValue="true") boolean replace);
	
	@JSONRpcResultParser("generateIntegers")
	protected int[] __generateIntegers(JSONRpcResult result, @JSONRpcParam("apiKey") String apiKey, @JSONRpcParam("n") int num, @JSONRpcParam("min") long min, @JSONRpcParam("max") long max, @JSONRpcParam(value="base") int base, @JSONRpcParam(value="replace") boolean replace) {
		JSONArray results = result.getJSON().getJSONObject("result").getJSONObject("random").getJSONArray("data");
		int[] parsed = new int[results.length()];
		if (base == 10)
			for (int i=0; i<parsed.length;i++)
				parsed[i] = results.getInt(i);
		else
			for (int i=0; i<parsed.length; i++)
				parsed[i] = Integer.parseInt(results.getString(i), base);
		
		return parsed;
	}
}

package com.divisors.projectcuttlefish.httpserver.api;

import java.time.Duration;

public interface RunnableService extends Runnable {
	boolean isRunning();
	
	boolean start() throws Exception;
	boolean stop() throws Exception;
	boolean stop(Duration timeout) throws Exception;
	boolean stopNow() throws Exception;
	
	void init() throws Exception;
	void destroy() throws Exception;
}

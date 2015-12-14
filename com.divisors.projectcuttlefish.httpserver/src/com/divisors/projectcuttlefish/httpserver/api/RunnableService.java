package com.divisors.projectcuttlefish.httpserver.api;

import java.time.Duration;
import java.util.concurrent.ExecutorService;

/**
 * Describes a service that is 
 * @author Liam
 *
 */
public interface RunnableService extends Runnable {
	/**
	 * Whether the service is running in any thread (active or blocking)
	 * @return if this service is running in another thread
	 */
	boolean isRunning();
	
	/**
	 * Start the service. This should start the service in another thread. Also, it SHOULD NOT
	 * automatically call {@link #init()} or initialize any resources (unless they are thread-dependent), as a direct call to {@link #run()} should be
	 * have the same effect, but merely run in the same thread as the caller.
	 * @throws Exception if there was a problem with starting the service
	 * @see #run()
	 * @throws IllegalStateException if this service cannot be started because {@link #init()} has not been called, it is already running, etc.
	 * @throws UnsupportedOperationException if this service cannot be started in another thread
	 */
	void start() throws Exception;
	void start(ExecutorService executor) throws Exception;
	/**
	 * Cease operation of this service. If this service accepts any input (e.g., a webserver)
	 * it SHOULD reject any input after this method is called. The service SHOULD attempt a
	 * soft shutdown, where any running tasks will be completed before closing, but no tasks
	 * will be started.
	 * @return if the service was shutdown. Returns FALSE if the service was not running
	 * at the time of this call, or if the service was being shutdown by another call.
	 * @throws Exception
	 */
	boolean shutdown() throws Exception;
	/**
	 * 
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	boolean shutdown(Duration timeout) throws Exception;
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	boolean shutdownNow() throws Exception;
	
	void init() throws Exception;
	void destroy() throws Exception;
}
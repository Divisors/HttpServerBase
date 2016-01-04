package com.divisors.projectcuttlefish.httpserver.api;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Describes a service that is 
 * @author mailmindlin
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
	/**
	 * Start with initializer
	 * @param initiator
	 * @throws Exception
	 * @see #start()
	 */
	void start(Consumer<? extends RunnableService> initiator) throws Exception;
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
	 * TODO finish documenting
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	boolean shutdown(Duration timeout) throws Exception;
	/**
	 * TODO finish documenting
	 * @return
	 * @throws Exception
	 */
	boolean shutdownNow() throws Exception;
	/**
	 * TODO finish documenting
	 * @throws Exception
	 */
	void init() throws Exception;
	/**
	 * TODO finish documenting
	 * @throws Exception
	 */
	void destroy() throws Exception;
}

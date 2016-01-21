package com.divisors.projectcuttlefish.httpserver.api;

import java.time.Duration;

/**
 * Describes a service that is 
 * @author mailmindlin
 */
public interface RunnableService extends Runnable {
	/**
	 * TODO finish documenting
	 * @return self
	 * @throws Exception
	 */
	RunnableService init() throws Exception;
	/**
	 * Start the service. This should start the service in another thread. Also, it SHOULD NOT
	 * automatically call {@link #init()} or initialize any resources (unless they are thread-dependent), as a direct call to {@link #run()} should be
	 * have the same effect, but merely run in the same thread as the caller.
	 * @return self
	 * 
	 * @throws Exception if there was a problem with starting the service
	 * @throws IllegalStateException if this service cannot be started because {@link #init()} has not been called, it is already running, etc.
	 * @throws UnsupportedOperationException if this service cannot be started in another thread
	 * 
	 * @see #run()
	 */
	RunnableService start() throws Exception;
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
	 * Destroy this service by releasing all resources used by this service, such as ports and
	 * file handles. As soon as this method is called, {@link #getState()} MUST return
	 * {@link RunnableServiceState#DESTROYED DESTROYED} or {@link RunnableServiceState#UNINITIALIZED},
	 * depending on whether a call to {@link #init()} is available. If an error is thrown in conjunction
	 * with this method being called, the state of this service SHOULD be DESTROYED.
	 * <p>
	 * After being destroyed, calls to {@link #start()} SHOULD throw an error.
	 * </p>
	 * @throws RuntimeException
	 * 		if there was a problem releasing any resource
	 * @throws IllegalStateException
	 * 		if this method is called when the state is NOT
	 * 		{@link RunnableServiceState#INITIALIZED INITIALIZED}
	 * @see #init()
	 */
	void destroy() throws RuntimeException;
	/**
	 * Get the current state. This method MUST not block, and SHOULD only return an instance variable.
	 * This method SHOULD be thread-safe (possibly by using AtomicReference's).
	 * @return this service's state
	 */
	ServiceState getState();
}

package com.divisors.projectcuttlefish.contentmanager.api;

import java.time.Duration;
import java.time.Instant;

import com.divisors.projectcuttlefish.uac.NotPermittedException;
import com.divisors.projectcuttlefish.uac.User;

public interface CachableView extends View {
	/**
	 * Whether the view string might have changed since the given instant. If uncertain,
	 * default to returning true, unless {@link #generate(User)} is computationally expensive.
	 * This method, if returning true, will force a cache dump. Note that this method doesn't
	 * nececarily refer to whether the last call of {@link #generate(User)} was after instant.
	 * <br/>
	 * Every call may choose to check the permissions of user.
	 * @param user that it was cached for (may be null if {@link #isUserDependent()} is false)
	 * @param instant time
	 * @return if the String returned by {@link #generate(User)} may have been changed since the given time
	 */
	boolean changedSince(User user, Instant instant) throws NotPermittedException;
	
	public Duration getMaxAge(User user, Instant instant) throws NotPermittedException;
}

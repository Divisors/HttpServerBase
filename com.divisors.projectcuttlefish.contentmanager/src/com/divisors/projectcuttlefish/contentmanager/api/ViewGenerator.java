package com.divisors.projectcuttlefish.contentmanager.api;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

import com.divisors.projectcuttlefish.uac.api.InvalidSessionException;
import com.divisors.projectcuttlefish.uac.api.NotPermittedException;
import com.divisors.projectcuttlefish.uac.api.Session;
import com.divisors.projectcuttlefish.uac.api.User;

public interface ViewGenerator {
	/**
	 * Get view name. Should be unique and immutable.
	 * @return view name
	 */
	String getName();
	/**
	 * Generate view data for user. This data should *not* be cached internally, because
	 * ViewManager should cache it if it according to its algorithm.
	 * @param user User to generate for. May be null if {@link #isUserDependent()} is false.
	 * @return generated view
	 * @throws NotPermittedException if the user is not permitted to access this view
	 * @throws IOException 
	 */
	default String generate(User user, String...args) throws InvalidSessionException, NotPermittedException, IOException {
		return generate(user, Optional.empty(), args);
	}
	/**
	 * @see #generate(User)
	 * @param user
	 * @param session
	 * @return
	 * @throws InvalidSessionException
	 * @throws NotPermittedException
	 * @throws IOException
	 */
	String generate(User user, Optional<Session> session, String...args) throws InvalidSessionException, NotPermittedException, IOException;
	/**
	 * Whether this view can be cached for all users, or just one
	 * @return it's cachability
	 */
	boolean isUserDependent();
}
/**
 * For simplified handling of errors on {@link com.projectcuttlefish.httpserver.api.HttpChannel HttpChannel}s.
 * <p>
 * Ideally, an implementation of HttpChannel will allow for individual handlers to throw HttpErrors. Upon
 * catching an error, it would trigger a {@link HttpErrorHandler} to respond to the request. This allows
 * a handler to throw a {@link Http404Error} if a resource isn't found, and an automated (and probably
 * standardized) error handler would serve a <code>HTTP 404</code> response.
 * </p>
 */
package com.divisors.projectcuttlefish.httpserver.api.error;
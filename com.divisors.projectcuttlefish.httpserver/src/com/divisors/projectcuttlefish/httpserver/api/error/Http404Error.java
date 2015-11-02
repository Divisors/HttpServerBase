package com.divisors.projectcuttlefish.httpserver.api.error;

public class Http404Error extends RuntimeException implements HttpError {
	private static final long serialVersionUID = 8770814358609764062L;
	
	@Override
	public int getErrorCode() {
		return 404;
	}
	
	

}

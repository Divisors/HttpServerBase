package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.List;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgent;

public interface ServableResource extends Resource, Predicate<UserAgent> {
	List<?> getCompression();
	
}

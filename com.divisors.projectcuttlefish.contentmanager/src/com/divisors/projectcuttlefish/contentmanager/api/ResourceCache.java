package com.divisors.projectcuttlefish.contentmanager.api;

public interface ResourceCache {
	ResourceCache put(Resource resource);
	Resource get(String name);
	Resource get(String name, String tag);
	boolean available(String name);
}
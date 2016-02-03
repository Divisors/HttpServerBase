package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.List;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;

public interface ResourceCache {
	ResourceCache put(Resource resource);
	Resource get(String name);
	Resource get(String name, String tag);
	Resource get(String name, List<String> compressionAvail);
	boolean available(String name);
}
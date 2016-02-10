package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;

public class ResourceCacheImpl implements ResourceCache {
	ConcurrentHashMap<String, Resource> resourceByName = new ConcurrentHashMap<>(),
			resourceByEtag = new ConcurrentHashMap<>();
//	Cache<String, ByteBuffer> memcache = CacheBuilder.newBuilder().build();
	@Override
	public ResourceCacheImpl put(Resource resource) {
		System.out.println("Loading resource '" + resource.getName() + "' as '" + resource.getName() + "'");
		resourceByName.put(resource.getName(), resource);
		return this;
	}

	@Override
	public Resource get(String name) {
		return resourceByName.get(name);
	}

	@Override
	public Resource get(String name, String tag) {
		Resource r = resourceByEtag.get(tag);
		if (r != null)
			return r;
		r = resourceByName.get(name);
		resourceByEtag.put(r.getEtag(), r);
		return r;
	}

	@Override
	public boolean available(String name) {
		return resourceByName.containsKey(name);
	}

	@Override
	public Resource get(String name, List<String> compressionAvail) {
		// TODO Auto-generated method stub
		return get(name);
	}

}

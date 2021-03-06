package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;
import com.divisors.projectcuttlefish.contentmanager.api.resource.ResourceTag;

public class ResourceCacheImpl implements ResourceCache {
	ConcurrentHashMap<String, Resource> resourceByName = new ConcurrentHashMap<>(),
			resourceByEtagStrong = new ConcurrentHashMap<>(),
			resourceByEtagWeak = new ConcurrentHashMap<>();
//	Cache<String, ByteBuffer> memcache = CacheBuilder.newBuilder().build();
	@Override
	public ResourceCacheImpl put(Resource resource) {
		ResourceTag tag = resource.getTag();
		System.out.println("Loading resource '" + tag.getName() + "' as '" + tag.getName() + "'");
		resourceByName.put(tag.getName(), resource);
		return this;
	}

	@Override
	public Resource get(String name) {
		return resourceByName.get(name);
	}

	@Override
	public Resource get(String name, String tag, boolean strong) {
		ConcurrentHashMap<String, Resource> resourceByEtag = (strong) ? this.resourceByEtagStrong : this.resourceByEtagWeak;
		Resource r = resourceByEtag.get(tag);
		if (r != null)
			return r;
		r = resourceByName.get(name);
		resourceByEtag.put(r.getEtag(strong), r);
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

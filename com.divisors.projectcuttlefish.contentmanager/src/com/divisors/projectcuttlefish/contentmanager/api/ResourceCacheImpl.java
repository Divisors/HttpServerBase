package com.divisors.projectcuttlefish.contentmanager.api;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class ResourceCacheImpl implements ResourceCache {
	ConcurrentHashMap<String, Resource> resources = new ConcurrentHashMap<>();
	Cache<String, ByteBuffer> memcache = CacheBuilder.newBuilder().build();
	@Override
	public ResourceCache put(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource get(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource get(String name, String tag) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean available(String name) {
		// TODO Auto-generated method stub
		return false;
	}

}

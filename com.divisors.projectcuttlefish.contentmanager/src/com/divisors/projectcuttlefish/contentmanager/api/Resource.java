package com.divisors.projectcuttlefish.contentmanager.api;

public interface Resource {
	String getName();
	boolean isRoot();
	Resource getParent();
	long estimateSize();
}

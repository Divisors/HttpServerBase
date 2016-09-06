package com.divisors.projectcuttlefish.contentmanager.api.resource;

import com.divisors.projectcuttlefish.httpserver.api.Version;

public class ResourceTag {
	String name;
	Version version;
	public ResourceTag(String name, Version version) {
		this.name = name;
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public Version getVersion() {
		return version;
	}
}

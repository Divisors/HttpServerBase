package com.divisors.projectcuttlefish.contentmanager.api.resource;

import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public interface Resource {
	ResourceTag getTag();
	String getName();
	long estimateSize();
	String getEtag();
	HttpResponsePayload toPayload();
}

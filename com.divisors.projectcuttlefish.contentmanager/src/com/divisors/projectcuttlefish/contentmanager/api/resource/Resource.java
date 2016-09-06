package com.divisors.projectcuttlefish.contentmanager.api.resource;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpContext;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public interface Resource {
	ResourceTag getTag();
	long estimateSize();
	String getEtag(boolean strong);
	HttpResponsePayload getPayload(HttpRequest request, HttpContext context);
}

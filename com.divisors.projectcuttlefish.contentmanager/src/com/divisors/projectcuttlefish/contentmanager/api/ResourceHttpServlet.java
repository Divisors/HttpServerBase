package com.divisors.projectcuttlefish.contentmanager.api;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpChannel;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseLineImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public class ResourceHttpServlet {
	static HashMap<Integer, String> responseNames = new HashMap<>();
	static {
		responseNames.put(404, "Not Found");
		responseNames.put(500, "Server Error");
	}
	HttpServer server;
	ResourceCache cache;
	public ResourceHttpServlet(HttpServer server) {
		this(server, new ResourceCacheImpl());
	}
	public ResourceHttpServlet(HttpServer server, ResourceCache cache) {
		this.server = server;
		this.cache = cache;
		server.onRequest(this::handleRequest);
	}
	public void handleRequest(HttpChannel channel, HttpRequest request) {
		String path = request.getRequestLine().getPath();
		if (path.startsWith("/"))
			path = path.substring(1);
		System.out.println("Searching for resource :'" + path + "'");
		Resource resource = cache.get(path);
		if (resource == null) {
			sendError(channel, request, 404);
			return;
		}
		HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(200, "OK"));
		HttpResponsePayload body = resource.toPayload();
		response.setBody(body);
		response.setHeader("Content-Length", "" + body.remaining())
			.setHeader("Content-Type", "text/html")
			.setHeader("Etag", resource.getEtag());
		channel.write(response);
	}
	protected void sendError(HttpChannel channel, HttpRequest request, int code) {
		HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(code, responseNames.getOrDefault(code, "Unknown")));
		response.setHeader("Content-Type", "text/plain");
		HttpResponsePayload body = HttpResponsePayload.wrap(ByteBuffer.wrap(("You got an error: " + code).getBytes()));
		response.setBody(body);
		response.setHeader("Content-Length", Long.toString(body.remaining()));
		channel.write(response);
	}
}

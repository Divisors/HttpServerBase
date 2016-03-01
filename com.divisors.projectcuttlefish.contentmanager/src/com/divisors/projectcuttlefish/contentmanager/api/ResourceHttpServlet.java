package com.divisors.projectcuttlefish.contentmanager.api;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpChannel;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseLineImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public class ResourceHttpServlet {
	static HashMap<Integer, String> responseNames = new HashMap<>();
	static {
		responseNames.put(400, "Bad Request");
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
		if (doCache(channel, request, path))
			return;
		System.out.println("Searching for resource: '" + path + "'");
		Resource resource = cache.get(path);
		if (resource == null) {
			System.out.println("\tCould not find resource: " + path);
			sendError(channel, request, 404);
			return;
		}
		HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(200, "OK"));
		HttpResponsePayload body = resource.toPayload();
		response.setBody(body);
		response.setHeader("Content-Length", "" + body.remaining())
			.setHeader("Content-Type", "text/html")
			.setHeader("Etag", '"' + resource.getEtag(true) + '"');
		channel.write(response);
	}
	protected void sendError(HttpChannel channel, HttpRequest request, int code) {
		String name = responseNames.getOrDefault(code, "Unknown");
		HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(code, name));
		response.setHeader("Content-Type", "text/plain");
		HttpResponsePayload body = HttpResponsePayload.wrap(ByteBuffer.wrap(("You got an error " + code + ": " + name).getBytes()));
		response.setBody(body);
		response.setHeader("Content-Length", Long.toString(body.remaining()));
		channel.write(response);
	}
	protected boolean doCache(HttpChannel channel, HttpRequest request, String path) {
		HttpHeaders headers = request.getHeaders();
		//if there's no etag, there's nothing to do
		if (!headers.containsKey("If-None-Match"))
			return false;
		
		
		HttpHeader ifNoneMatchHeader = headers.getHeader("If-None-Match");
		if (ifNoneMatchHeader.getValue().size() > 1) {
			sendError(channel, request, 400);
			return true;
		}
		
		String tag = ifNoneMatchHeader.first().trim();
		if (tag.length() <= 2) {
			System.err.println("Invalid Etag: " + tag);
			return false;
		}
		
		boolean isStrong = !tag.substring(0, 2).equalsIgnoreCase("W/");
		if (!isStrong)
			tag = tag.substring(2);
		
		tag = tag.substring(1, tag.length() - 1);//remove quotes around tag
		
		
		Resource resource = cache.get(path, tag, isStrong);
		
		if (resource != null && resource.getEtag(isStrong).equals(tag)) {
			//send 304 NOT MODIFIED
			HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(304, "Not Modified"));
			response.addHeader("Content-Length", "0");
			
			StringBuilder tagOut = new StringBuilder((isStrong?0:2) + 2 + tag.length());
			if (isStrong) {
				tagOut.append('"');
			} else {
				tagOut.append("W/\"");
			}
			tagOut.append(tag);
			tagOut.append('"');
			
			response.addHeader("Etag", tagOut.toString());
			channel.write(response);
			return true;
		}
		//the resource's etag has changed (probably because it was updated), so don't send a 304
		return false;
	}
}

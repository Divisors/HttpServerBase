package com.mindlin.http;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestLine {
	protected final String method, protocol, path;
	public RequestLine (String from) {
		String[] ssv = from.split(" ");
		method = ssv[0];
		path = ssv[1];
		protocol = ssv[2];
	}
	public String getMethod() {
		return method;
	}
	public String getProtocol() {
		return protocol;
	}
	public String getPath() {
		return path;
	}
	public URL getURL() throws MalformedURLException {
		return new URL(path);
	}
	@Override
	public String toString() {
		return method + " " + path + " " + method;
	}
}

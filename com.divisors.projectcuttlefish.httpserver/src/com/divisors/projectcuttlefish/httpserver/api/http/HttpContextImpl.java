package com.divisors.projectcuttlefish.httpserver.api.http;

import java.net.SocketAddress;

/**
 * Implementation of HttpContext for {@link HttpChannelImpl}
 * @author mailmindlin
 */
public class HttpContextImpl implements HttpContext {
	protected final HttpChannelImpl channel;
	protected HttpProtocol protocol = HttpProtocol.UNKNOWN;
	protected HttpContextImpl(HttpChannelImpl channel) {
		this.channel = channel;
	}
	@Override
	public SocketAddress getAddress() {
		return channel.source.getRemoteAddress();
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public HttpContext setProperty(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getProperty(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpProtocol getProtocol() {
		return protocol;
	}

}

package com.divisors.projectcuttlefish.httpserver.api.http;

import javax.net.ssl.SSLEngine;

import com.divisors.projectcuttlefish.httpserver.api.http2.HelloWorldHttp1Handler;
import com.divisors.projectcuttlefish.httpserver.api.http2.HelloWorldHttp2Handler;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2OrHttpChooser;

public class NettyProtocolChooserHandler extends Http2OrHttpChooser {

	protected NettyProtocolChooserHandler() {
		super(100 * 1024);
	}

	@Override
	protected SelectedProtocol getProtocol(SSLEngine engine) {
		String[] protocol = engine.getSession().getProtocol().split(":");
		if (protocol != null && protocol.length > 1) {
			SelectedProtocol selectedProtocol = SelectedProtocol.protocol(protocol[1]);
			System.err.println("Selected Protocol is " + selectedProtocol);
			return selectedProtocol;
		}
		return SelectedProtocol.UNKNOWN;
	}

	@Override
	protected ChannelHandler createHttp1RequestHandler() {
		return new HelloWorldHttp1Handler();
	}

	@Override
	protected Http2ConnectionHandler createHttp2RequestHandler() {
		return new HelloWorldHttp2Handler();
	}
}

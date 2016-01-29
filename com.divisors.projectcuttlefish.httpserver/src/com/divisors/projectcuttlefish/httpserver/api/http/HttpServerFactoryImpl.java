package com.divisors.projectcuttlefish.httpserver.api.http;

import java.io.IOException;
import java.net.SocketAddress;

public class HttpServerFactoryImpl implements HttpServerFactory {

	@Override
	public HttpServer createServer(SocketAddress addr) throws IOException {
		return new HttpServerImpl().listenOn(addr);
	}

}

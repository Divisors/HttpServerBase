package com.divisors.projectcuttlefish.httpserver.api.http;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.http2.Http2ServerInitializer;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import reactor.bus.EventBus;

public class NettyHttpServer implements HttpServer {
	protected ServerBootstrap bootstrap;
	protected Channel channel;
	protected final int port = 8080;
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;

	@Override
	public NettyHttpServer init() throws Exception {
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		this.bootstrap = new ServerBootstrap();
		this.bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		this.bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new NettyProtocolChooserHandler());
		return this;
	}

	@Override
	public HttpServer start(Consumer<? super HttpServer> initializer) throws IOException, IllegalStateException {
		initializer.accept(this);
		return start();
	}

	@Override
	public HttpServer start() throws IOException, IllegalStateException {
		try {
			channel = bootstrap.bind(port).sync().channel();

	        System.err.println("Open your HTTP/2-enabled web browser and navigate to localhost:" + port + '/');

	        channel.closeFuture().sync();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public Action onConnect(Consumer<HttpChannel> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action onRequest(BiConsumer<HttpChannel, HttpRequest> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpServer dispatchOn(EventBus bus) throws IllegalStateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventBus getBus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public ServiceState getState() {
		return state.get();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shutdown() {
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
		return true;
	}

	@Override
	public boolean shutdown(Duration timeout) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shutdownNow() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroy() throws RuntimeException {
		// TODO Auto-generated method stub

	}
}

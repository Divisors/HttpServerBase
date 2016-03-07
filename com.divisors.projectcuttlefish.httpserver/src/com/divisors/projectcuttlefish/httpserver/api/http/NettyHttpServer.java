package com.divisors.projectcuttlefish.httpserver.api.http;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.http2.HelloWorldHttp2Handler;
import com.divisors.projectcuttlefish.httpserver.api.http2.Http2ServerInitializer;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import reactor.bus.Event;
import reactor.bus.EventBus;

public class NettyHttpServer implements HttpServer {
	protected ServerBootstrap bootstrap;
	protected Channel channel;
	protected final int port = 8080;
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	protected EventBus bus;
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
			.childHandler(new Http2ServerInitializer(null));
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
			run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public Action onConnect(Consumer<HttpChannel> handler) {
		return new RegistrationCancelAction(this.bus.on($t("http.connect"), event -> handler.accept(((Event<HttpChannel>)event).getData())));
	}

	@Override
	public Action onRequest(BiConsumer<HttpChannel, HttpRequest> handler) {
		return new RegistrationCancelAction(this.bus.on($t("http.request"), event -> handler.accept(((Event<HttpRequest>)event).getData())));
	}

	@Override
	public NettyHttpServer dispatchOn(EventBus bus) throws IllegalStateException {
		this.bus = bus;
		return this;
	}

	@Override
	public EventBus getBus() {
		return bus;
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
		state.compareAndSet(ServiceState.STARTING, ServiceState.RUNNING);
		try {
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			state.compareAndSet(ServiceState.RUNNING, ServiceState.INITIALIZED);
		}
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

	protected class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {

		/**
		 * Configure the pipeline for a cleartext upgrade from HTTP to HTTP/2.
		 */
		@Override
		public void initChannel(SocketChannel ch) {
			HttpServerCodec sourceCodec = new HttpServerCodec();
			HttpServerUpgradeHandler.UpgradeCodec upgradeCodec = new Http2ServerUpgradeCodec(
					new HelloWorldHttp2Handler());
			HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec,
					Collections.singletonList(upgradeCodec), 65536);

			ch.pipeline().addLast(sourceCodec);
			ch.pipeline().addLast(upgradeHandler);
			ch.pipeline().addLast(new UserEventLogger());
		}

	}

	/**
	 * Class that logs any User Events triggered on this channel.
	 */
	protected static class UserEventLogger extends ChannelHandlerAdapter {
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
			System.out.println("User Event Triggered: " + evt);
			ctx.fireUserEventTriggered(evt);
		}
	}
}

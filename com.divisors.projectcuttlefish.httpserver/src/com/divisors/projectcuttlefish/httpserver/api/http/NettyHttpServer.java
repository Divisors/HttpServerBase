package com.divisors.projectcuttlefish.httpserver.api.http;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.SocketAddress;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.net.ssl.SSLEngine;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.http2.HelloWorldHttp2Handler;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2OrHttpChooser;
import io.netty.handler.codec.http2.Http2ServerUpgradeCodec;
import io.netty.handler.codec.http2.Http2OrHttpChooser.SelectedProtocol;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import reactor.bus.Event;
import reactor.bus.EventBus;

public class NettyHttpServer implements HttpServer {
	protected SslContext sslCtx;
	protected ServerBootstrap bootstrap;
	protected Channel channel;
	protected final int port = 8080;
	protected final AtomicReference<ServiceState> state = new AtomicReference<>(ServiceState.UNINITIALIZED);
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	protected EventBus bus;
	protected ExecutorService executor;

	@Override
	public NettyHttpServer init() throws Exception {
		bootstrap = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup();
		this.bootstrap = new ServerBootstrap();
		this.bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		this.bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
		.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new NettyHttpServerInitializer());
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
			if (executor == null) {
				run();
			} else {
				executor.submit(this);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public Action onConnect(Consumer<HttpChannel> handler) {
		return new RegistrationCancelAction(
				this.bus.on($t("http.connect"), event -> handler.accept(((Event<HttpChannel>) event).getData())));
	}

	@Override
	public Action onRequest(BiConsumer<HttpChannel, HttpRequest> handler) {
		return null;// new
					// RegistrationCancelAction(this.bus.on($t("http.request"),
					// event ->
					// handler.accept(((Event<HttpRequest>)event).getData())));
	}

	@Override
	public NettyHttpServer dispatchOn(EventBus bus) throws IllegalStateException {
		this.bus = bus;
		return this;
	}

	public NettyHttpServer runOn(ExecutorService executor) throws IllegalStateException {
		this.executor = executor;
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
		executor.shutdownNow();
		return true;
	}

	@Override
	public boolean shutdown(Duration timeout) throws InterruptedException {
		return shutdown();
	}

	@Override
	public boolean shutdownNow() throws Exception {
		return shutdown();
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
			System.out.println("Initializing channel");
			if (sslCtx == null) {
				HttpServerCodec sourceCodec = new HttpServerCodec();
				HttpServerUpgradeHandler.UpgradeCodec upgradeCodec = new Http2ServerUpgradeCodec(
						new HelloWorldHttp2Handler());
				HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec,
						Collections.singletonList(upgradeCodec), 65536);

				ch.pipeline().addLast(sourceCodec);
				ch.pipeline().addLast(upgradeHandler);
				ch.pipeline().addLast(new UserEventLogger());
			} else {
				ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), new HttpProtocolNegotiator());
			}
		}
	}

	/**
	 * Negotiates HTTP/1 or HTTP/2 connection
	 * @author mailmindlin
	 *
	 */
	protected class HttpProtocolNegotiator extends Http2OrHttpChooser {
		protected HttpProtocolNegotiator() {
			super(16 * 1024);
		}

		@Override
		protected SelectedProtocol getProtocol(SSLEngine engine) {
			String[] protocol = engine.getSession().getProtocol().split(":");
			if (protocol != null && protocol.length > 1) {
				SelectedProtocol selectedProtocol = SelectedProtocol.protocol(protocol[1]);
				System.out.println("Selected Protocol is " + selectedProtocol);
				return selectedProtocol;
			}
			System.out.println("Unknown protocol");
			return SelectedProtocol.UNKNOWN;
		}

		@Override
		protected ChannelHandler createHttp1RequestHandler() {
			System.out.println("Creating request handler for HTTP 1");
			return new Http1ChannelHandler();
		}

		@Override
		protected Http2ConnectionHandler createHttp2RequestHandler() {
			System.out.println("Creating request handler for HTTP 2");
			return null;
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

	protected class Http1ChannelHandler implements ChannelHandler {
		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Added");
		}

		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Removed");
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			cause.printStackTrace();
		}

		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Registered");
		}

		@Override
		public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Unregistered");
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Active");
		}

		@Override
		public void channelInactive(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Inactive");
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			System.out.println("Read: " + msg);
		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
			System.out.println("Read complete");
		}

		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			System.out.println("User triggered: " + evt);
		}

		@Override
		public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
				throws Exception {
			System.out.println("Bound: " + localAddress + "; " + promise);
		}

		@Override
		public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress,
				ChannelPromise promise) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void read(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
			// TODO Auto-generated method stub

		}

		@Override
		public void flush(ChannelHandlerContext ctx) throws Exception {
			// TODO Auto-generated method stub

		}
	}
}

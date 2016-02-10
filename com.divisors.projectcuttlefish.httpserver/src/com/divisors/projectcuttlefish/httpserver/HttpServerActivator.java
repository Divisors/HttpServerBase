package com.divisors.projectcuttlefish.httpserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.reactivestreams.Processor;

import com.divisors.projectcuttlefish.httpserver.api.StandardChannelOptions;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServerImpl;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseLineImpl;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerFactory;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentDetector;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.processor.RingBufferProcessor;

/**
 * Currently a working echo server, it binds to a random port between 1000 and 65536, and opens the default webbrowser.
 * 
 * This supports restarting and stuff.
 * 
 * @author mailmindlin
 *
 */
public class HttpServerActivator implements BundleActivator {
	protected static HttpServerActivator INSTANCE;
	public static HttpServerActivator getInstance() {
		return INSTANCE;
	}
	BundleContext ctx;
	ServiceRegistration<?> httpServerFactoryServiceRegistration;
	ServiceRegistration<?> serverFactoryServiceRegistration;
	TcpServer tcp;
	public HttpServer http;
	UserAgentDetector uaDetector;
	Processor<Event<?>,Event<?>> processor;
	
	/**
	 * Standard http header text
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		INSTANCE = this;
		this.ctx = context;
		try {
			System.out.println("Initializing: ProjectCuttlefish|HttpServer");
			processor = RingBufferProcessor.create("pc.server.1", 32);
			uaDetector = new UserAgentDetector();
			uaDetector.init();
			http = new HttpServerImpl(EventBus.create(processor), Executors.newCachedThreadPool())
				.init()
				.start(server -> {
					try {
						((HttpServerImpl)server)
							.listenOn(new InetSocketAddress(8085))
							.onConnect((channel) -> {
//								channel.onRead((request) -> {
//									if (request.getHeaders().containsKey("User-Agent"))
//										uaDetector.apply(request.getHeader("User-Agent").first());
//									HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(request.getRequestLine().getHttpVersion(),200,"OK"))
//											.addHeader("Server","PC-0.0.6")
//											.addHeader("Content-Type","text/plain; charset=utf-8");
//									
//									StringBuilder responseText = new StringBuilder().append("Hello, World!");
//									HttpResponsePayload payload = HttpResponsePayload.wrap(ByteBuffer.wrap(responseText.toString().getBytes()));
//									response.addHeader("Content-Length",""+payload.remaining()).setBody(payload);
//									channel.write(response);
//								});
								System.err.println("New channel: " + channel);
							});
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				});
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye!");
		try {
			if (tcp != null)
				tcp.shutdownNow();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		INSTANCE = null;
	}
	public BundleContext getContext() {
		return ctx;
	}
	public TcpServer testTCP(Processor<Event<?>,Event<?>> processor) throws IllegalStateException, IOException, Exception {
		return TcpServerFactory.getInstance().createServer(new InetSocketAddress("localhost", 8080))
				.init()
				.start(server-> {
					((TcpServerImpl)server)
						.dispatchOn(EventBus.create(processor))
						.runOn(Executors.newCachedThreadPool())
						.onConnect((channel)-> {
							System.out.println("Connected to channel @ " + channel.getRemoteAddress() + ", id=" + channel.getConnectionID());
							channel.onRead(data->{
								//print out the data sent to this
								System.out.println("Recieved request from " + channel.getRemoteAddress());
								
								HttpRequest request = HttpRequest.parse(data);
								
								String text = new String(data.array());
								HttpResponse response = new HttpResponseImpl(new HttpResponseLineImpl(request.getRequestLine().getHttpVersion(),200,"OK"))
										.setHeader("Server", "PC-0.0.6")
										.setHeader("Content-Type", "text/plain; charset=utf-8");
								
								byte[] output = new StringBuilder(text)
										.append("\n=====BYTES=====\n")
										.append(FormatUtils.bytesToHex(data.array(), true, 0))
										.toString()
										.getBytes();
								
								response.setHeader("Content-Length", "" + output.length);
								
								channel.setOption(StandardChannelOptions.CLOSE_AFTER_WRITE, true)
										.write(response.serialize())
										.write((ByteBuffer)ByteBuffer.allocate(output.length).put(output).flip());
							});
						});
					//try to open the default browser to send a request to this server
//					if (Desktop.isDesktopSupported())
//						try {
//							Desktop.getDesktop().browse(new URI("http://localhost:" + ((InetSocketAddress)server.getAddress()).getPort()));
//						} catch (URISyntaxException | IOException e) {
//							e.printStackTrace();
//						}
				});
	}
}

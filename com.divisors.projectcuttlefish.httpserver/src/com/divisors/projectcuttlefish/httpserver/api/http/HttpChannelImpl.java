package com.divisors.projectcuttlefish.httpserver.api.http;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.ChannelOption;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.response.StandardHttpResponseSerializer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannel;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

import reactor.bus.Event;
import reactor.bus.registry.Registration;
import reactor.fn.Consumer;
import reactor.fn.tuple.Tuple;

/**
 * Implementation of 
 * <br/>
 * Note that this is non-operational (it does literally nothing)
 * @author mailmindlin
 * @see HttpChannel
 */
public class HttpChannelImpl implements HttpChannel {
	protected final TcpChannel source;
	protected final HttpServerImpl server;
	protected final AtomicBoolean open = new AtomicBoolean(true);
	protected final List<Registration<Object, Consumer<? extends Event<?>>>>subscriptions = new LinkedList<>();
	public HttpChannelImpl(TcpChannel source, HttpServerImpl server) {
		server.bus.notify(Tuple.<String,Long>of("http.connect", source.getConnectionID()), Event.wrap(this));
		this.source = source;
		this.server = server;
		source.onRead((buffer) -> {
			byte[] arr = ByteUtils.toArray(buffer);
			FormatUtils.bytesToHex(arr, true, -1);//TODO figure out why this makes it not crash...
			HttpRequest request = HttpRequest.parse(buffer);
			System.out.println("Dispatching request...");
			server.bus.notify(Tuple.<String, Long>of("http.request", this.source.getConnectionID()), Event.wrap(request));
		});
	}
	@Override
	public Channel<HttpRequest, HttpResponse> write(HttpResponse data) {
		source.write(StandardHttpResponseSerializer.serialize(data));
		return this;
	}

	@Override
	public Channel<HttpRequest, HttpResponse> onRead(Consumer<HttpRequest> handler) {
		this.subscriptions.add(server.bus.on($t("http.request", this.source.getConnectionID()), event -> handler.accept((HttpRequest) event.getData())));
		return this;
	}

	@Override
	public boolean isOpen() {
		return open.get();
	}

	@Override
	public void close() throws Exception {
		if (!open.compareAndSet(false, true))
			throw new IllegalStateException("Channel was closed/detatched");
		this.source.close();
		System.out.println("HTTP::Closing channel #" + this.source.getConnectionID());
	}

	@Override
	public HttpServerImpl getHttpServer() {
		return this.server;
	}

	@Override
	public Optional<TcpChannel> getTcp() {
		return Optional.of(source);
	}

	@Override
	public HttpContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <E> Channel<HttpRequest, HttpResponse> setOption(ChannelOption<E> key, E value) {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	public long getConnectionID() {
		return this.source.getConnectionID();
	}

}

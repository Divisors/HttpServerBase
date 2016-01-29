package com.divisors.projectcuttlefish.httpserver.client;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.ChannelOption;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.util.RegistrationCancelAction;

import reactor.bus.Event;
import reactor.bus.registry.Registration;
import reactor.fn.Consumer;
import reactor.fn.tuple.Tuple;

public class HttpClientChannel implements Channel<HttpResponse, HttpRequest> {
	protected final HttpClient client;
	protected final TcpClientChannel tcp;
	protected final List<Action> shutdownActions = new LinkedList<>();
	protected HttpClientChannel(HttpClient client, TcpClientChannel tcp) {
		this.client = client;
		this.tcp = tcp;
		shutdownActions.add(this.tcp.onConnect((x) -> {
			System.out.println("HTTPc::onTCPConnect");
			client.bus.notify(Tuple.of("http.accept", getConnectionID()), Event.wrap(this));
		}));
		shutdownActions.add(this.tcp.onRead((data)-> {
			System.out.println("HTTPc::onTCPRead");
			HttpResponse response = HttpResponse.parse(data);
			client.bus.notify(Tuple.of("http.response", getConnectionID()), Event.wrap(response));
		}));
	}
	@Override
	public HttpClientChannel write(HttpRequest data) {
		System.out.println("HTTPc:: writing request");
		ByteBuffer b = data.serialize();
		tcp.write(b);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Action onRead(Consumer<HttpResponse> handler) {
		Registration<?,?> registration = client.bus.on($t("http.response",getConnectionID()), event->handler.accept(((Event<HttpResponse>)event).getData()));
		Action action = new RegistrationCancelAction(registration);
		this.shutdownActions.add(action);
		return action;
	}
	@SuppressWarnings("unchecked")
	public Action onConnect(Consumer<TcpClientChannel> handler) {
		Registration<?,?> registration = client.bus.on($t("http.accept",getConnectionID()), event->handler.accept(((Event<TcpClientChannel>)event).getData()));
		registration.cancelAfterUse();// Connect should be called only once, so clean it up soon
		Action action = new RegistrationCancelAction(registration);
		this.shutdownActions.add(action);
		return action;
	}
	public long getConnectionID() {
		return tcp.getConnectionID();
	}
	@Override
	public boolean isOpen() {
		return tcp.isOpen();
	}

	@Override
	public <E> HttpClientChannel setOption(ChannelOption<E> key, E value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		for (Action action : shutdownActions)
			if (action.isAvailable())
				action.act();
		this.tcp.close();
	}
	public void connect() throws IOException {
		System.out.println("HTTPc::Connecting");
		this.client.doConnect(this);
	}
}

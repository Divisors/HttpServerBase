package com.divisors.projectcuttlefish.contentmanager.api.gh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestBuilder;
import com.divisors.projectcuttlefish.httpserver.client.HttpClient;
import com.divisors.projectcuttlefish.httpserver.client.HttpClientChannel;

import reactor.bus.Event;
import reactor.bus.EventBus;

public class GithubApiService {
	public static final int MAX_CHANNELS = 2;
	protected static final SocketAddress GITHUB = new InetSocketAddress("api.github.com", 80);
	protected final HttpClient client;
	protected final Set<HttpClientChannel> ghChannels = new HashSet<>();
	protected final BlockingQueue<HttpClientChannel> availGhChannels = new LinkedBlockingQueue<>();
	protected final EventBus bus;
	public GithubApiService(HttpClient client) {
		this(client, client.getBus());
	}
	public GithubApiService(HttpClient client, EventBus bus) {
		this.client = client;
		this.bus = bus;
	}
	public void init() throws Exception {
		if (client.getState() == ServiceState.UNINITIALIZED)
			client.init();
	}
	public Future<JSONObject> getRepositoriesByOwner(String user) throws IOException {
		query(new HttpRequestBuilder()
				.setMethod("GET")
				.setPath("https://api.github.com/users/" + user + "/repos")
				.addHeader("User-Agent", "PC-0.0.6")
				.addHeader("Accept", "application/json")
				.addHeader("Accept-Language", "en-US,en;q=0.8")
				.build());
		return null;
	}
	protected HttpClientChannel provisionChannel() throws IOException, InterruptedException {
		HttpClientChannel channel = availGhChannels.poll();
		if (channel != null)
			return channel;
		if (ghChannels.size() < MAX_CHANNELS) {
			System.out.println("Opening channel " + ghChannels.size() + " to api.github.com");
			channel = client.open(GITHUB);
			this.ghChannels.add(channel);
			channel.connect();
			return channel;
		} else {
			return null;
		}
	}
	protected void query(HttpRequest request) throws IOException {
		HttpClientChannel channel = client.open(GITHUB);
		channel.onConnect((c) -> channel.write(request));
		channel.onRead((response)-> {
			System.out.println(response);
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		channel.connect();
	}
	protected void doQuery(Event<HttpRequest> request) {
		try {
			HttpClientChannel channel = provisionChannel();
			channel.write(request.getData());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

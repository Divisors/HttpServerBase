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
	protected static final SocketAddress GITHUB = new InetSocketAddress("192.30.252.127", 443);
	protected final HttpClient client;
	protected final HashSet<HttpClientChannel> channels = new HashSet<>();
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
	public GithubUser getUserByName(String name) {
		return new GithubUser(this, name);
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
	public void query(GithubApiObject object) throws IOException {
		HttpRequestBuilder request = new HttpRequestBuilder();
		request.setMethod("GET");
		request.setPath(object.getPath());
		request.addHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36")
			.addHeader("Accept", "text/html,application/json;q=0.9,*/*;q=0.8")
			.addHeader("Accept-Language", "en-US,en;q=0.8")
			.addHeader("Host", "api.github.com");
		query(request.build());
	}
	protected void query(HttpRequest request) throws IOException {
		System.out.println("Querying: " + request);
		HttpClientChannel channel = client.open(GITHUB);
		channel.onConnect((c) -> {
			System.out.println("Writing request: " + request);
			channel.write(request);
		});
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
			HttpClientChannel channel = client.open(GITHUB);
			channel.write(request.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

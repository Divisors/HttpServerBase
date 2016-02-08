package com.divisors.projectcuttlefish.httpserver;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.reactivestreams.Processor;

import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequestBuilder;
import com.divisors.projectcuttlefish.httpserver.client.HttpClient;
import com.divisors.projectcuttlefish.httpserver.client.HttpClientChannel;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.processor.RingBufferProcessor;

public class HttpClientTest {
	
	@Test
	public void test() throws Exception {
		Processor<Event<?>,Event<?>> processor = RingBufferProcessor.create("testClient", 32);
		HttpClient client = new HttpClient(EventBus.create(processor), Executors.newCachedThreadPool());
		client.init();
		client.start();
		HttpClientChannel channel = client.open(new InetSocketAddress(8080));
		System.err.println("Testing connection");
		channel.connect();
		System.err.println("\tDone");
		System.err.println("Sending request...");
		HttpRequest request = new HttpRequestBuilder()
				.setMethod("GET")
				.setPath("test");
		request.getHeaders().add("X-Foo","bar");
		channel.write(request.immutable());
		System.err.println("Done");
		Thread.sleep(5000);
	}
	
}

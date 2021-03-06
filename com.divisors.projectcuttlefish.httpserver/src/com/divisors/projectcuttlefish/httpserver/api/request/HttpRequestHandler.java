package com.divisors.projectcuttlefish.httpserver.api.request;

import java.util.function.BiConsumer;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpChannel;

public interface HttpRequestHandler extends BiConsumer<HttpRequest, HttpChannel>{

}

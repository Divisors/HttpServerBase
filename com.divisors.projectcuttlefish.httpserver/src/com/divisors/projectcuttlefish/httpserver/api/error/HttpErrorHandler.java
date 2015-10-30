package com.divisors.projectcuttlefish.httpserver.api.error;

import java.util.function.BiFunction;

import com.divisors.projectcuttlefish.httpserver.api.HttpExchange;

public interface HttpErrorHandler extends BiFunction<HttpError, HttpExchange, Boolean> {

}

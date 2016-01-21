package com.divisors.projectcuttlefish.httpserver.api.error;

import java.util.function.BiFunction;

import com.divisors.projectcuttlefish.httpserver.api.http.HttpChannel;

public interface HttpErrorHandler extends BiFunction<HttpError, HttpChannel, Boolean> {

}

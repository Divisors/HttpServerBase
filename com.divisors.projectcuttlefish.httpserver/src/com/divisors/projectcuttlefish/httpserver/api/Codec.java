package com.divisors.projectcuttlefish.httpserver.api;

/**
 * Specifies how to map from one channel type to another
 * @author mailmindlin
 *
 * @param <IN1> channel 1 input type
 * @param <OUT1> channel 1 output type
 * @param <IN2> channel 2 input type
 * @param <OUT2> channel 2 output type
 */
@Deprecated
public interface Codec<IN1, OUT1, IN2, OUT2> {
	OUT1 encode(OUT2 stuff);
	IN2 decode(IN1 stuff);
}

package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.function.Function;
import java.util.function.Predicate;

public interface ResourceCompressor extends Predicate<Resource>, Function<Resource, Resource>, Comparable<ResourceCompressor> {
}

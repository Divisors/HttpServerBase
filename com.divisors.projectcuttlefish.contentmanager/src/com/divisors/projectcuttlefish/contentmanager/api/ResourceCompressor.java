package com.divisors.projectcuttlefish.contentmanager.api;

import java.util.function.Function;
import java.util.function.Predicate;

import com.divisors.projectcuttlefish.contentmanager.api.resource.Resource;

public interface ResourceCompressor extends Predicate<Resource>, Function<Resource, Resource>, Comparable<ResourceCompressor> {
}

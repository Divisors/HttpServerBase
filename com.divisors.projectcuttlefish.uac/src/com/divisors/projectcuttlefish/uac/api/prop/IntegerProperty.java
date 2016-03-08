package com.divisors.projectcuttlefish.uac.api.prop;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class IntegerProperty implements Property {
	protected volatile TsVPair value;
	
	public IntegerProperty() {
		this(0);
	}
	
	public IntegerProperty(int value) {
		this(value, timestamp);
	}
	
	public IntegerProperty(int value, Instant timestamp) {
		this.value = value;
		this.timestamp = new AtomicReference<>(timestamp);
	}
	
	public boolean modifiedSince(Instant time) {
		return this.timestamp.compareTo(time) > 0;
	}
	
	public boolean updateIfNotModifiedSince(Instant notModifiedAfter, int value) {
		
	}
	protected class TsVPair {
		final int value;
		final Instant timestamp;
		TsVPair(int value, Instant timestamp) {
			this.value = value;
			this.timestamp = timestamp;
		}
	}
}

package com.divisors.projectcuttlefish.uac.api;

import java.time.Instant;

public abstract class PcAssignment implements PcEntity {
	public abstract Instant getAssigned();
	public abstract Instant getDue();
}

package com.divisors.projectcuttlefish.uac.api;

import java.util.UUID;

public interface Session extends PcEntity {
	boolean isValid();
	PcUser getUser();
}

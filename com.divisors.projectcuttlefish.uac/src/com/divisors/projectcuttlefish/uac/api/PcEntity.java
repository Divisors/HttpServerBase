package com.divisors.projectcuttlefish.uac.api;

import java.util.UUID;

/**
 * Represents something that 'can be acted upon', or 'can act upon' something else.
 */
public interface PcEntity {
	public static UUID nextEntityUUID() {
		return null;//TODO finish
	}
	UUID getEntityId();
}
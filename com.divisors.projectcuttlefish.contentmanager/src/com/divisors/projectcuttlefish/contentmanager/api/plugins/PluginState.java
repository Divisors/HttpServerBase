package com.divisors.projectcuttlefish.contentmanager.api.plugins;

public enum PluginState {
	REGISTERED,
	LOADING,
	LOADED,
	ACTIVATING,
	ACTIVE,
	DEACTIVATING,
	UNLOADING,
	UPGRADING_FROM,
	UPGRADING_TO;
}

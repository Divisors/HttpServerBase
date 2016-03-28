package com.divisors.projectcuttlefish.contentmanager.api.plugins;

public interface Plugin {
	/**
	 * Get this plugin's name
	 * @return name
	 */
	String getName();
	String getVersion();
	String getDescription();
	void load();
	void activate();
	void deactivate();
	void unload();
	default void beforeUpgradeFrom() {}
	default void upgradeFrom() {
		deactivate();
	}
	default void afterUpgradeFrom() {}
	default void beforeUpgradeTo() {}
	default void upgradeTo() {
		activate();
	}
	default void afterUpgradeTo() {}
}

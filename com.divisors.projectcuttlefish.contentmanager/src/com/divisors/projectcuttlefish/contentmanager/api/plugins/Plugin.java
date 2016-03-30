package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;

import org.json.JSONArray;
import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.api.Version;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.bus.registry.Registration;
import reactor.fn.Consumer;
import reactor.fn.tuple.Tuple;

/**
 * 
 * @author mailmindlin
 */
public class Plugin implements EventTarget {
	/**
	 * Convert a JSONArray of strings to a list of strings
	 * @param arr JSONArray
	 * @return list of strings
	 */
	protected static List<String> stringArrayToList(JSONArray arr) {
		if (arr == null)
			return Collections.emptyList();
		ArrayList<String> result = new ArrayList<>(arr.length());
		for (Object entry : arr)
			result.add(entry.toString());
		return result;
	}
	protected final String name;
	protected final Version version;
	protected final String description;
	protected final Path homeDir;
	protected final List<String> scripts;
	protected final List<String> permissions;
	protected final List<String> optionalPermissions;
	protected final List<String> storage;
	protected final long id;
	protected ScriptEngine engine;
	protected EventBus bus;
	public Plugin(JSONObject data, Path home, long id, ScriptEngine engine, EventBus bus) {
		this.name = data.getString("name");
		this.version = new Version(data.getString("version"));
		this.description = data.optString("description", "[no description]");
		this.homeDir = home;
		this.scripts = stringArrayToList(data.optJSONArray("scripts"));
		JSONObject permissions = data.optJSONObject("permissions");
		if (permissions != null) {
			this.permissions = stringArrayToList(permissions.optJSONArray("required"));
			this.optionalPermissions = stringArrayToList(permissions.optJSONArray("optional"));
		} else {
			this.permissions = this.optionalPermissions = Collections.emptyList();
		}
		this.storage = stringArrayToList(data.optJSONArray("storage"));
		this.id = id;
		this.engine = engine;
		this.bus = bus;
	}
	/**
	 * Get this plugin's name
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	public Version getVersion() {
		return this.version;
	}
	public String getDescription() {
		return this.description;
	}
	public long getId() {
		return this.id;
	}
	public EventBus getBus() {
		return this.bus;
	}
	public ScriptEngine getEngine() {
		return this.engine;
	}
	public Path getHome() {
		return this.homeDir;
	}
	public void triggerEvent(String name, Object...args) {
		System.out.println("PLUGIN " + getId() + ":\tInvoking event " + name);
		bus.notify(Tuple.of("plugins",getId(),name), new Event<>(args));
	}
	@Override
	public void addEventListener(String eventName, Consumer<? extends Event<?>> handler, boolean onlyOnce) {
		System.out.println("PLUGIN " + getId() + ":\tAdding event listener for " + eventName);
		 Registration<Object, Consumer<? extends Event<?>>> registration = getBus().on($t("plugins",getId(),eventName), handler);
		 if (onlyOnce)
			 registration.cancelAfterUse();
	}
}

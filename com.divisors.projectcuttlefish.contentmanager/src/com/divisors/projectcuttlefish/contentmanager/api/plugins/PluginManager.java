package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornScriptEngine;

public class PluginManager {
	public static final PluginManager INSTANCE = new PluginManager();
	
	protected ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();
	public void loadPlugin(Path json) {
		Path home = json.getParent();
		
	}
	public void loadJS(Path p) {
	}

	public void loadJS(String js) throws ScriptException {
		NashornScriptEngine engine = getEngine();
		engine.eval(js);
	}

	protected NashornScriptEngine getEngine() {
		NashornScriptEngine engine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("Nashorn");
		engine.put("PluginManager", this);
		engine.put("Plugin", Plugin.class);
		return engine;
	}
	
	public void register(Plugin proto) {
		plugins.put(proto.getName(), proto);
		System.out.println("Loaded plugin " + proto.getName());
		proto.load();
	}
}

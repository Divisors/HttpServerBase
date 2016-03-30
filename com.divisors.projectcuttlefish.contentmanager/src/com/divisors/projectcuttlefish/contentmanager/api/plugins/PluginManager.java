package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.text.ChangedCharSetException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.tuple.Tuple;

/**
 * 
 * @author mailmindlin
 */
public class PluginManager {
	public static PluginManager INSTANCE;
	
	protected final AtomicLong nextID = new AtomicLong(0);
	protected ConcurrentHashMap<Long, Plugin> plugins = new ConcurrentHashMap<>();
	protected ExecutorService executor;
	protected EventBus bus;
	public PluginManager(EventBus bus) {
		this.bus = bus;
		bus.on($t("plugins.scriptinit"), this::loadScripts);
	}
	public boolean loadPlugin(Path json) {
		System.out.println("Attempting to load manifest from " + json);
		Path home = json.getParent();
		JSONObject manifestData;
		try (BufferedReader br = Files.newBufferedReader(json)) {
			manifestData = new JSONObject(new JSONTokener(br));
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find manifest file @ " + json);
			e.printStackTrace();
			return false;
		} catch (ChangedCharSetException | CharacterCodingException | CharConversionException | UnsupportedEncodingException e){
			System.err.println("There was a problem with parsing the text in the manifest file @ " + json);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("There was a problem reading the manifest file @ " + json);
			e.printStackTrace();
			return false;
		} catch (JSONException e) {
			System.err.println("Unable to parse manifest @ " + json + " (is it valid JSON?)");
			e.printStackTrace();
			return false;
		}
		System.out.println(manifestData);
		long id = nextID.getAndIncrement();
		ScriptEngine engine = getEngine();
		Plugin p = new Plugin(manifestData, home, id, engine, bus);
		this.plugins.put(id, p);
		engine.put("Plugin", p);
		System.out.println("Initializing plugin #" + id + ": " + p.getName());
		bus.notify(Tuple.of("plugins.scriptinit", id));
		System.out.println("Done initializing");
		return true;
	}
	public void loadScripts(Event<?> ev) {
		System.out.print("Loading plugin #");
		long id = (Long) ((Tuple)ev.getKey()).get(1);
		
		Plugin plugin = this.getPluginByID(id);
		System.out.println(new StringBuilder().append(plugin.getId()).append('(').append(plugin.getName()).append(')'));
		for (String script : plugin.scripts) {
			Path scriptPath;
			try {
				scriptPath = plugin.getHome().resolve(script).normalize();
			} catch (InvalidPathException e) {
				System.err.println(new StringBuilder(40).append("Invalid script path ").append(script).append(" (Plugin #").append(plugin.getId()).append(')'));
				e.printStackTrace();
				continue;
			}
			System.out.println("Loading script @ " + scriptPath);
			if (!checkPluginFileAccess(plugin, scriptPath)) {
				System.err.println(new StringBuilder(70).append("Invalid permissions to load script from ").append(scriptPath).append(" (Plugin #").append(plugin.getId()).append(')'));
				System.err.println("If you need to load this script, you can request the permission 'file.script.loadSpecial'");
				continue;//should it die?
			}
			try (BufferedReader br = Files.newBufferedReader(scriptPath)) {
				plugin.getEngine().eval(br);
			} catch (FileNotFoundException e) {
				System.err.println(new StringBuilder(45).append("Unable to find script @ ").append(scriptPath).append(" (Plugin #").append(plugin.getId()).append(')'));
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		plugin.triggerEvent("load");
	}
	public Map<Long, Plugin> getPlugins() {
		return Collections.unmodifiableMap(this.plugins);
	}
	public boolean checkPluginFileAccess(Plugin plugin, Path path) {
		return path.startsWith(plugin.getHome());
	}
	public String loadTextFile(Path path) throws FileNotFoundException, IOException {
		try (BufferedReader br = Files.newBufferedReader(path)) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line.trim()).append("\n");
			return sb.toString();
		}
	}
	public Plugin getPluginByID(long id) {
		return this.plugins.get(id);
	}
	protected ScriptEngine getEngine() {
		System.out.print("Getting engine...");
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("Nashorn");
		engine.put("PluginManager", this);
		engine.put("console", Console.getInstance());
		System.out.println("SUCCESS");
		return engine;
	}
}

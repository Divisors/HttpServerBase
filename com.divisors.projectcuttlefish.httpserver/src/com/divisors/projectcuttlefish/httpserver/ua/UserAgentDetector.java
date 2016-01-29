package com.divisors.projectcuttlefish.httpserver.ua;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.Activator;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser.ParsedUAToken;

public class UserAgentDetector implements Function<String, UserAgent>, RunnableService {
	/**
	 * Load a resource from the bundle's jar
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected static String loadFromJar(String path) throws IOException {
		System.out.println("Loading: '" + path + "'");
		StringBuffer sb = new StringBuffer();
		URL url = Activator.getInstance().getContext().getBundle().getEntry(path);
		if (url == null)
			throw new FileNotFoundException(location);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))) {
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line).append("\n");
		}
		System.out.println("\tDone loading");
		return sb.toString();
	}
	protected static String loadLocal(String path) {
		IPath stateLocation = Platform.getStateLocation(Activator.getInstance().getContext().getBundle());
		System.out.println(stateLocation);
		return null;
	}
	protected static boolean saveLocal(String path, String data) {
		return false;
	}
	UserAgentParser parser = new UserAgentParser();
	List<UABrowser> browsers;
	ConcurrentHashMap<String, UserAgent> cache = new ConcurrentHashMap<>();
	public UserAgentDetector() throws JSONException {
		//load resources
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	public void init() throws IOException, JSONException {
		JSONObject browserMeta = new JSONObject(load("/src/com/divisors/projectcuttlefish/httpserver/resources/ua/browser.json"));
		JSONArray browserInfo = browserMeta.getJSONArray("browsers");
		this.browsers = new LinkedList<>();
		for (int i=0; i < browserInfo.length(); i++) {
			UABrowser browser = new UABrowser(browserInfo.getJSONObject(i));
			System.out.println("Loaded browser: " + browser.getName());
			browsers.add(browser);
		}
	}
	@Override
	public UserAgent apply(String s) {
		ParsedUAToken[] tokens = parser.tokenize(s);
		for (UABrowser browser : browsers) {
			if (browser.test(tokens)) {
				System.out.println("Browser: " + browser.getName());
				return null;
			}
		}
		return null;
	}
	static class UAPattern {
		public String name;
		public Pattern pattern;
		public UAPattern(String name, String pattern) {
			this.name = name;
			this.pattern = Pattern.compile(pattern);
		}
	}
}

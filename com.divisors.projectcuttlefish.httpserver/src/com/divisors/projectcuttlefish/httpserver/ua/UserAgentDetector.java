package com.divisors.projectcuttlefish.httpserver.ua;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.divisors.projectcuttlefish.httpserver.Activator;
import com.divisors.projectcuttlefish.httpserver.api.RunnableService;
import com.divisors.projectcuttlefish.httpserver.api.ServiceState;
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
			throw new FileNotFoundException(path);
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
	ConcurrentHashMap<String, UserAgent> cache = new ConcurrentHashMap<>();
	List<UABrowser> browsers;
	public UserAgentDetector() {
	}
	@Override
	public UserAgentDetector init() throws IOException, JSONException {
		JSONObject browserMeta = new JSONObject(loadFromJar("/src/com/divisors/projectcuttlefish/httpserver/resources/ua/browser.json"));
		JSONArray browserInfo = browserMeta.getJSONArray("browsers");
		this.browsers = new ArrayList<>(browserInfo.length());
		for (int i=0; i < browserInfo.length(); i++) {
			UABrowser browser = new UABrowser(browserInfo.getJSONObject(i));
			System.out.println("Loaded browser: " + browser.getName());
			browsers.add(browser);
		}
		return this;
	}
	@Override
	public UserAgentDetector start() throws Exception {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	public UserAgent apply(String s) {
		{
			UserAgent result = this.cache.get(s);
			if (result != null) {
				System.out.println("Browser (cached): " + result.getBrowser());
				return result;
			}
		}
		ParsedUAToken[] tokens = parser.tokenize(s);
		UABrowser browser;
		for (UABrowser browserCandidate : browsers) {
			if (browserCandidate.test(tokens)) {
				browser = browserCandidate;
				System.out.println("Browser: " + browser.getName());
				break;
			}
		}
		
		UAOperatingSystem os;
		
		return null;
	}
	
	protected Map<String, String> applyParseRules(UABrowser browser, ParsedUAToken[] tokens) {
		//TODO finish
		return null;
	}
	
	@Override
	public void run() {	}
	@Override
	public boolean shutdown() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean shutdown(Duration timeout) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean shutdownNow() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void destroy() throws RuntimeException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public ServiceState getState() {
		// TODO Auto-generated method stub
		return null;
	}
}

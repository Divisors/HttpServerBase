package com.divisors.projectcuttlefish.httpserver;

import java.io.IOException;

import org.json.JSONException;
import org.junit.Test;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgentDetector;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser;

public class UAParseTest {
	
	@Test
	public void test() {
		UserAgentParser parser = new UserAgentParser();
		parser.apply("Mozilla/5.0 (Macintosh; U; Mac OS X 10_6_1; en-US) AppleWebKit/530.5 (KHTML, like Gecko) Chrome/ Safari/530.5");
	}
	@Test
	public void test2() throws JSONException, IOException {
		new UserAgentDetector();
	}
	
}

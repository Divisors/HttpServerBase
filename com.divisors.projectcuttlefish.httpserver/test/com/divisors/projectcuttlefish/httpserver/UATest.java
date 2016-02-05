package com.divisors.projectcuttlefish.httpserver;

import org.junit.Test;

import com.divisors.projectcuttlefish.httpserver.ua.UserAgentDetector;
import com.divisors.projectcuttlefish.httpserver.ua.UserAgentParser;

public class UATest {

	@Test
	public void test() throws Exception {
		assert(true == false);
		String uaString = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_1 like Mac OS X; en-us) AppleWebKit/532.9 (KHTML, like Gecko) Version/4.0.5 Mobile/8B117 Safari/6531.22.7";
		UserAgentParser parser = new UserAgentParser();
		parser.apply(uaString);
		try {
		UserAgentDetector detector = new UserAgentDetector();
		detector.init();
		detector.apply(uaString);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
//		fail("Not yet implemented");
	}
}

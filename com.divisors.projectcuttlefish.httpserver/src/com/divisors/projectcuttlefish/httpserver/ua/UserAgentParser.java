package com.divisors.projectcuttlefish.httpserver.ua;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user agents
 * @author mailmindlin
 *
 */
public class UserAgentParser implements Function<String, UserAgent> {
	/**
	 * Online demo @ <a href="http://www.regexpal.com/?fam=93936">regexpal.com/?fam=93936</a>.
	 */
	public static final Pattern tokenizer = Pattern.compile("(([A-Za-z]+)(\\/?([a-zA-Z0-9\\.]+)?))( \\(((([A-Za-z0-9\\ \\_\\-\\,\\.]+); ?)*([A-Za-z0-9\\ \\_\\-\\,\\.]+))\\))?");
	@Override
	public UserAgent apply(String ua) {
		ParsedUAToken[] tokens = tokenize(ua);
		System.out.println(Arrays.toString(tokens));
		UASecurity security = UASecurity.UNKNOWN;
		String language = null;
		String platform = null;
		UAOperatingSystem system = null;
		if (tokens.length >= 1) {
			switch (tokens[0].details.length) {
				case 4:
					//get language
					language = tokens[0].details[3];
				case 3:
					platform = tokens[0].details[2];
				case 2:
					switch (tokens[0].details[1]) {
						case "U":
							security = UASecurity.STRONG;
							break;
						case "I":
							security = UASecurity.WEAK;
							break;
						case "N":
							security = UASecurity.NONE;
							break;
					}
				case 1:
					platform = tokens[0].details[0];
			}
		}
		System.out.println("Security: " + security);
		System.out.println("Language: " + language);
		System.out.println("Platform: " + platform);
		return null;
	}
	protected ParsedUAToken[] tokenize(String ua) {
		List<ParsedUAToken> result = new LinkedList<>();
		Matcher m = tokenizer.matcher(ua);
		while (m.find())
			result.add(new ParsedUAToken(m.group(2), m.group(4), m.group(6)));
		return result.toArray(new ParsedUAToken[result.size()]);
	}
	public static class ParsedUAToken {
		/**
		 * ProductName
		 */
		final String name;
		/**
		 * ProductVersion
		 */
		final String version;
		/**
		 * ProductDetails
		 */
		final String[] details;
		
		public ParsedUAToken(String name, String version, String[] details) {
			this.name = name;
			this.version = version;
			this.details = details;
		}
		public ParsedUAToken(String name, String version, String details) {
			this.name = name;
			this.version = version;
			if (details == null)
				this.details = new String[0];
			else
				this.details = details.split("; ");
		}
		public String getName() {
			return name;
		}

		public String getVersion() {
			return version;
		}

		public String[] getDetails() {
			return details;
		}

		@Override
		public String toString() {
			return new StringBuilder("ParsedUAToken{name=\"")
				.append(this.name)
				.append("\"; version=\"")
				.append(this.version)
				.append("\"; details=")
				.append(Arrays.toString(details))
				.append("}")
				.toString();
		}
	}
}

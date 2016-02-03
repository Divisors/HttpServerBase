package com.divisors.projectcuttlefish.httpserver.api.request;

import java.nio.ByteBuffer;
import java.util.AbstractMap;

import com.divisors.projectcuttlefish.httpserver.api.Mutable;
import com.divisors.projectcuttlefish.httpserver.api.error.ParseException;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils.ByteBufferTokenizer;
import com.divisors.projectcuttlefish.httpserver.util.Constants;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

/**
 * Wrapper for HTTP request
 * 
 * @author mailmindlin
 *
 */
public interface HttpRequest extends Mutable<HttpRequest> {
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_TRACE = "TRACE";
	public static final String METHOD_OPTIONS = "OPTIONS";
	public static final String METHOD_CONNECT = "CONNECT";
	public static final String HTTP_1 = "HTTP/1.0";
	public static final String HTTP_1_1 = "HTTP/1.1";
	public static final String HTTP_2 = "HTTP/2";
	/**
	 * Parse byte array
	 * @param data
	 * @return parsed request
	 * @throws ParseException
	 */
	public static HttpRequest parse(ByteBuffer data) throws ParseException {
		ByteBufferTokenizer tokenizer = new ByteBufferTokenizer(Constants.HTTP_NEWLINE, data);
		HttpRequestBuilder builder = new HttpRequestBuilder();
		ByteBuffer token;
		
		//parse request line
		if ((token = tokenizer.next()) == null)
			throw new ParseException("Token is null (while parsing request line)");
		String reqLine = new String(ByteUtils.toArray(token)).trim();
		String[] sections = reqLine.split(" ");//TODO optimize
		builder.setMethod(sections[0])
				.setPath(sections[1])
				.setHttpVersion(sections[2].trim());
		
		System.out.print("Parsing headers...");
		parseHeaders(tokenizer, builder.getHeaders());
		System.out.println("Done");
		
		//TEST: print to console
		System.out.println("\t=> "+builder.getRequestLine());
		builder.getHeaders().entrySet().stream()
			.flatMap(entry->entry.getValue().stream()
				.map(value->(new AbstractMap.SimpleEntry<>(entry.getKey(), value))))
			.map(entry->("\t=> "+entry.getKey()+": "+entry.getValue()))
			.forEach(System.out::println);
		return builder.build();
	}
	public static void parseHeaders(ByteBufferTokenizer tokenizer, HttpHeaders headers) throws ParseException {
		ByteBuffer token;
		while ((token = tokenizer.next()) != null && token.remaining() > 5) {//the smallest possible header is 5 bytes ('K:V\r\n')
			//last 2 bytes are '\r\n', so this effectively does the same thing as String#trim()
			byte[] header = new byte[token.remaining() - 2];
			token.get(header);
			int keyEnd = -1, length = header.length - 1;
			search:
			for (int i=1; i<length; i++)
				if (header[i] == ':') {
					keyEnd = i;
					break search;
				}
			String key = new String(header, 0, keyEnd);
			String value = new String(header, keyEnd, header.length - keyEnd).substring(2);//TODO remove substring, add to keyEnd
			headers.add(key, value);
		}
		if (token != null && token.remaining() > 2)
			throw new ParseException("Unknown bytes found in parsing headers: "+FormatUtils.bytesToHex(ByteUtils.toArray(token), true, -1));
	}
	default String getMethod() {
		return getRequestLine().getMethod();
	}

	default String getPath() {
		return getRequestLine().getPath();
	}

	default String getHttpVersion() {
		return getRequestLine().getHttpVersion();
	}

	HttpRequestLine getRequestLine();
	
	HttpHeaders getHeaders();
	default HttpHeader getHeader(String key) {
		return getHeaders().getHeader(key);
	}
	HttpRequest addHeader(HttpHeader header);
	HttpRequest addHeader(String key, String...values);
	HttpRequest setHeader(HttpHeader header);
	HttpRequest setHeader(String key, String...values);
	HttpRequest removeHeader(String key);
	
	@Override
	default ImmutableHttpRequest immutable() {
		return new ImmutableHttpRequest(this);
	}
	default ByteBuffer serialize() {
		StringBuilder headers = new StringBuilder(getRequestLine().toString())
				.append("\r\n")
				.append(getHeaders().toString())
				.append("\r\n");
		return ByteBuffer.wrap(headers.toString().getBytes());
	}
}

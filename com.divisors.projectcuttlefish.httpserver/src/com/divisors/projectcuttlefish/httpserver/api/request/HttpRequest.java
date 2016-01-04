package com.divisors.projectcuttlefish.httpserver.api.request;

import java.nio.ByteBuffer;
import java.util.AbstractMap;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.HttpHeaders;
import com.divisors.projectcuttlefish.httpserver.api.Mutable;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils.ByteBufferTokenizer;

/**
 * Wrapper for HTTP request
 * 
 * @author mailmindlin
 *
 */
public interface HttpRequest extends Mutable<HttpRequest> {
	public static final byte[] newline = new byte[]{'\r','\n'};
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
	 * @return
	 */
	public static HttpRequest parse(byte[] data) {
		ByteBufferTokenizer tokenizer = new ByteBufferTokenizer(newline, data.length);//TODO maybe pool
		tokenizer.put(data);
		HttpRequestBuilder builder = new HttpRequestBuilder();
		ByteBuffer token;
		//parse request line
		{
			
			if ((token = tokenizer.next()) == null)
				throw new IllegalStateException("Token is null");
			String[] sections = new String(ByteUtils.toArray(token)).split(" ");//TODO optimize
			builder.setRequestLine(new HttpRequestLineBuilder()
					.setMethod(sections[0])
					.setPath(sections[1])
					.setVersion(sections[2].trim())
					.build());
			System.out.println("Got request line: "+builder.requestLine);
		}
		System.out.println("Parsing headers...");
		{
			HttpHeaders headers = builder.getHeaders();
			while ((token = tokenizer.next()) != null && token.remaining() > 2) {
				String header = new String(ByteUtils.toArray(token)).trim();
				//TODO better parsing/optimization
				String key = header.substring(0, header.indexOf(':'));
				String value = header.substring(header.indexOf(':')+1).trim();
				headers.add(key, value);
			}
			System.out.println("Parsed headers:");
			headers.entrySet().stream()
				.flatMap(entry->entry.getValue().stream()
						.map((value)->(new AbstractMap.SimpleEntry<>(entry.getKey(), value))))
				.map(entry->("=>"+entry.getKey()+": "+entry.getValue()))
				.forEach(System.out::println);
		}
		return builder.build();
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
	
	default ImmutableHttpRequest immutable() {
		return new ImmutableHttpRequest(this);
	}
}

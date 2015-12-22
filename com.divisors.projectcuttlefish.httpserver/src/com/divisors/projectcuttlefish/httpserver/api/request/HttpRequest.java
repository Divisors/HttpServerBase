package com.divisors.projectcuttlefish.httpserver.api.request;

import java.nio.ByteBuffer;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.HttpHeader;
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
			while ((token = tokenizer.next()) != null && token.remaining() > 2) {
				String header = new String(ByteUtils.toArray(token)).trim();
				//TODO better parsing/optimization
				String key = header.substring(0, header.indexOf(':'));
				String value = header.substring(header.indexOf(':')+1).trim();
				System.out.println("HEADER "+key+'/'+value);
			}
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
	
	List<HttpHeader> getHeaders();
	default HttpHeader getHeader(String key) {
		for (HttpHeader header : getHeaders())
			if (header.getKey().equalsIgnoreCase(key))
				return header;
		return null;
	}
	
	default ImmutableHttpRequest immutable() {
		return new ImmutableHttpRequest(this);
	}
}

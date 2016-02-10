package com.divisors.projectcuttlefish.httpserver.api.response;

import java.nio.ByteBuffer;
import java.util.AbstractMap;

import com.divisors.projectcuttlefish.httpserver.api.Mutable;
import com.divisors.projectcuttlefish.httpserver.api.error.ParseException;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeader;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpHeaders;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils;
import com.divisors.projectcuttlefish.httpserver.util.ByteUtils.ByteBufferTokenizer;
import com.divisors.projectcuttlefish.httpserver.util.Constants;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

/**
 * Represents a HTTP response
 * @author mailmindlin
 * @see HttpResponseLine
 * @see ImmutableHttpResponse
 */
public interface HttpResponse extends Mutable<ImmutableHttpResponse> {
	public static final int RC_PRECONDITION_FAILED = 412;
	/**
	 * Parse a ByteBuffer
	 * @param data
	 * @return parsed response
	 * TODO test
	 */
	public static HttpResponse parse(ByteBuffer data) throws ParseException {
		ByteBufferTokenizer tokenizer = new ByteBufferTokenizer(Constants.HTTP_NEWLINE, data);
		ByteBuffer token;
		System.out.println(new String(ByteUtils.toArray(data.duplicate())));
		System.out.println(FormatUtils.bytesToHex(data.duplicate(), true));
		// Parse response line
		if ((token = tokenizer.next()) == null)
			throw new ParseException("Token was null (while parsing response line).");
		String[] sections = new String(ByteUtils.toArray(token)).split(" ");//TODO optimize
		HttpResponseImpl result = new HttpResponseImpl(new HttpResponseLineImpl(sections[0], Integer.parseInt(sections[1]), sections[2]));
		
		// Parse headers
		HttpRequest.parseHeaders(tokenizer, result.getHeaders());
		System.out.println("Done");
		
		//TEST: print to console
		System.out.println("\t=> "+result.getResponseLine());
		result.getHeaders().entrySet().stream()
			.flatMap(entry->entry.getValue().stream()
				.map(value->(new AbstractMap.SimpleEntry<>(entry.getKey(), value))))
			.map(entry->("\t=> "+entry.getKey()+": "+entry.getValue()))
			.forEach(System.out::println);
		return result;
	}
	
	HttpResponseLine getResponseLine();
	
	HttpHeaders getHeaders();
	HttpHeader getHeader(String key);
	HttpResponse addHeader(HttpHeader header);
	HttpResponse addHeader(String key, String...values);
	HttpResponse setHeader(HttpHeader header);
	HttpResponse setHeader(String key, String...values);
	HttpResponse removeHeader(String key);
	
	HttpResponse setBody(HttpResponsePayload payload);
	HttpResponsePayload getBody();
	
	@Override
	default boolean isMutable() {
		return true;
	}
	@Override
	default ImmutableHttpResponse immutable() {
		return new ImmutableHttpResponse(getResponseLine(), getHeaders(), getBody());
	}
	default ByteBuffer serialize() {
		StringBuilder headers = new StringBuilder(getResponseLine().toString())
				.append("\r\n")
				.append(getHeaders().toString())
				.append("\r\n");
		byte[] bytes = headers.toString().getBytes();
		HttpResponsePayload body = getBody();
		if (body == null) {
			return ByteBuffer.wrap(bytes);
		} else {
			ByteBuffer b = ByteBuffer.allocate(bytes.length + (int)getBody().remaining());//TODO: fix for big things
			b.put(bytes);
			getBody().drainTo(b::put);
			b.flip();
			return b;
		}
	}
}

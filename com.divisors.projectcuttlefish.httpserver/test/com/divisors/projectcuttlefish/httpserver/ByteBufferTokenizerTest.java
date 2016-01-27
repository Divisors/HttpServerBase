package com.divisors.projectcuttlefish.httpserver;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.divisors.projectcuttlefish.httpserver.util.ByteUtils.ByteBufferTokenizer;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

@SuppressWarnings("unused")
public class ByteBufferTokenizerTest {

	@Test
	public void test() {
		ByteBufferTokenizer tokenizer = new ByteBufferTokenizer(new byte[]{0x00,(byte) 0xFF},16);
		tokenizer.put(0x01,0x00,0xFF,0x00,0x01,0x00,0xFF,0xFF,0x00,0xFF,0x01,0x02);
		ByteBuffer result;
		while ((result = tokenizer.next()) != null)
			System.out.println(FormatUtils.bytesToHex(result, true));
		System.out.println("Scraps: " + FormatUtils.bytesToHex(tokenizer.remaining(), true));
//		fail("Not yet implemented");
	}

}

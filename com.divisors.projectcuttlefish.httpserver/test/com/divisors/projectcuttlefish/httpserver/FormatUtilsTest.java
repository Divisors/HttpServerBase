package com.divisors.projectcuttlefish.httpserver;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

public class FormatUtilsTest {

	@Test
	public void testBytesToHex() {
//		byte[] bytes = ByteBuffer.allocate(12).putInt(0x00FF00FF).putInt(0x01234567).putInt(0x89ABCDEF).array();
//		String hexString = FormatUtils.bytesToHex(bytes);
//		System.out.println("Hex: "+hexString);
//		assertEquals("Message",hexString.length(), 12 * 2);
//		assertTrue("Message 2",hexString.equalsIgnoreCase("00FF00FF0123456789ABCDEF"));
	}
	@Test
	public void testBytesToHex2() {
		byte[] bytes = ByteBuffer.allocate(12).putInt(0x00FF00FF).putInt(0x01234567).putInt(0x89ABCDEF).array();
		System.out.println("\t=======S1=========");
		System.out.println(FormatUtils.bytesToHex(bytes, false, 0));
		System.out.println("\t=======S2=========");
		System.out.println(FormatUtils.bytesToHex(bytes, true, 0));
		System.out.println("\t=======S3=========");
		System.out.println(FormatUtils.bytesToHex(bytes, false, 4));
		System.out.println("\t=======S4=========");
		System.out.println(FormatUtils.bytesToHex(bytes, true, 4));
	}
}

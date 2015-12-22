package com.divisors.projectcuttlefish.httpserver.util;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class FormatUtils {
	/**
	 * Array of hex chars for fast lookup
	 */
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	/**
	 * Converts array of 
	 * @param bytes
	 * @return
	 */
	public static char[] bytesToHexChars(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return hexChars;
	}
	/**
	 * Convert byte array to string of hex digits
	 * @param bytes bytes to convert (unsigned)
	 * @return hex digit strings
	 */
	public static String bytesToHex(byte[] bytes) {
		return new String(bytesToHexChars(bytes));
	}
	public static String bytesToHex(byte[] bytes, boolean separate, int rowWidth) {
		if (separate) {
			char[] result = new char[bytes.length * 3];//3 characters per byte (byte is 2 hex digits + space)
			for (int i=0; i<bytes.length; i++) {
				int b = bytes[i] & 0xFF;
				int offset = i * 3;
				result[offset] = hexArray[b>>>4];
				result[offset + 1] = hexArray[ b & 0xF];
				result[offset + 2] = ' ';
			}
			if (rowWidth > 0) {
				int numLines = bytes.length / rowWidth;
				if (bytes.length > numLines * rowWidth)
					numLines++;
				for (int i=1; i<numLines; i++)
					result[(rowWidth * 3) * i - 1] = '\n';
			}
			//TODO remove space/newline at the end
			return new String(result);
		} else {
			if (rowWidth > 0) {
				char[] hexChars = bytesToHexChars(bytes);
				int numLines = bytes.length / rowWidth;
				if (bytes.length > numLines * rowWidth)
					numLines++;
				System.out.println(""+bytes.length + " / " + numLines);
				char[] result = new char[bytes.length * 2 + numLines - 1];
				//TODO fix outofbounds when # of bytes is not multiple of rowWidth
				for (int line = 0; line < numLines; line++)
					System.arraycopy(hexChars, line * rowWidth * 2, result, (rowWidth * 2 + 1) * line, rowWidth * 2);
				//System.arraycopy(hexChars, rowWidth * 2 * numLines, result, (rowWidth * 2 + 1) * numLines, hexChars.length - rowWidth * 2 * numLines);
				for (int i=1;i<numLines;i++)
					result[(rowWidth * 2 + 1) * i - 1] = '\n';
				System.out.println(Arrays.toString(hexChars));
				System.out.println(Arrays.toString(result));
				return new String(result);
			}
			return bytesToHex(bytes);
		}
	}
	public static String bytesToHex(ByteBuffer buf, boolean split) {
		byte[] arr = new byte[buf.remaining()];
		buf.get(arr);
		return bytesToHex(arr, split, 0);
	}
}

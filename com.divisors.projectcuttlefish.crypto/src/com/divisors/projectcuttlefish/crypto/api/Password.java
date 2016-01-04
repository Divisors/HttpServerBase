package com.divisors.projectcuttlefish.crypto.api;

/**
 * For securely storing sensitive information
 * @author mailmindlin
 */
public final class Password {
	private final char[] chars;
	public Password(char[] chars) {
		final int length = chars.length;
		this.chars = new char[length];
		System.arraycopy(chars, 0, this.chars, 0, length);
		System.arraycopy(new char[length],0,chars,0,length);//empty array
	}
	public char[] toCharArray() {
		final int length = this.chars.length;
		char[] result = new char[length];
		System.arraycopy(this.chars, 0, result, 0, length);
		return result;
	}
	public Password copy() {
		return new Password(chars);
	}
	public void clear() {
		final int length = this.chars.length;
		System.arraycopy(new char[length], 0, this.chars, 0, length);
	}
}

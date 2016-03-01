package com.divisors.projectcuttlefish.crypto.api;

/**
 * For securely storing sensitive information
 * @author mailmindlin
 */
public final class Password {
	private final char[] chars;
	public Password(char[] chars) {
		if (chars == null) {
			this.chars = null;
			return;
		}
		
		final int length = chars.length;
		this.chars = new char[length];
		System.arraycopy(chars, 0, this.chars, 0, length);
		System.arraycopy(new char[length],0,chars,0,length);//empty array
	}
	public char[] toCharArray() {
		if (this.chars == null)
			return null;
		
		final int length = this.chars.length;
		char[] result = new char[length];
		System.arraycopy(this.chars, 0, result, 0, length);
		return result;
	}
	
	public Password copy() {
		return new Password(chars);
	}
	
	public void clear() {
		if (this.chars == null)
			return;
		final int length = this.chars.length;
		System.arraycopy(new char[length], 0, this.chars, 0, length);
	}
}

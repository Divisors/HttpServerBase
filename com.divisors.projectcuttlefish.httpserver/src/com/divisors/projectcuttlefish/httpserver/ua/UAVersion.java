package com.divisors.projectcuttlefish.httpserver.ua;

public class UAVersion {
	protected int[] numbers;
	public UAVersion(int[] numbers) {
		this.numbers = numbers;
	}
	public int getMajor() {
		return numbers[0];
	}
	public int getMinor() {
		return (numbers.length>=2) ? numbers[1] : 0;
	}
	public int getPatch() {
		return (numbers.length>=3) ? numbers[2] : 0;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int number : numbers)
			sb.append(number).append('.');
		sb.delete(sb.length()-2, sb.length());//TODO: test
		return sb.toString();
	}
}

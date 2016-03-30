package com.divisors.projectcuttlefish.httpserver.api;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple way to use semantic versioning
 * @see {@link semver.org}
 * @author mailmindlin
 */
public class Version implements Comparable<Version> {
	public static final int compareStr(String a, String b) {
	/**
	 * Tests whether a given string is a valid integer
	 */
	public static final Predicate<String> isInteger = Pattern.compile("\\d+").asPredicate();
	/**
	 * Tests whether a given string is formed from 
	 */
	public static final Predicate<String> asciiAlphaNumericTest = Pattern.compile("[\\dA-Za-z\\-]+(\\.[\\dA-Za-z\\-]+)*").asPredicate();
	/**
	 * A regular expression to parse a valid version string
	 */
	public static final Pattern semanticVersion = Pattern
			.compile("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(\\.\\d+)*(-(?<prerel>[0-9A-Za-z\\-\\.]+))?(\\+(?<meta>[0-9A-Za-z\\-\\.]+))?$");
	
	/**
	 * Compares strings in the method described by <a href="http://semver.org/#spec-item-11">semver.org � 11</a>.
	 * <p>
	 * This method compares dot-separated identifiers from left to right in the strings as follows:
	 * <ol>
	 * 		<li>Identifiers consisting of only digits are compared numerically</li>
	 * 		<li>Identifiers with letters or hyphens are compared lexically in ASCII sort order</li>
	 * 		<li>Numeric identifiers always have lower precedence than non-numeric identifiers.</li>
	 * 		<li>A larger set of pre-release fields has a higher precedence than a smaller set, if all of the preceding identifiers are equal</li>
	 * </ol>
	 * </p>
	 * If <code>a.equals(b)</code> is true, this method will return 0.
	 * 
	 * @param a
	 *            first string to compare
	 * @param b
	 *            second string to compare
	 * @return their comparison
	 */
		final String[] aPreTokens = a.split("\\.");
		final String[] bPreTokens = b.split("\\.");
		final int len = Math.min(aPreTokens.length, bPreTokens.length);
		for (int i = 0; i < len; i++) {
			String aPreToken = aPreTokens[i];
			String bPreToken = bPreTokens[i];
			if (aPreToken.equals(bPreToken))
				continue;
			boolean aIsNumber = isInteger.test(aPreToken);
			boolean bIsNumber = isInteger.test(bPreToken);
			if (aIsNumber && bIsNumber) {
				//"identifiers consisting of only digits are compared numerically" 
				int aNumber = Integer.parseInt(aPreToken);
				int bNumber = Integer.parseInt(bPreToken);
				if (aNumber != bNumber)
					return aNumber - bNumber;
			} else if (aIsNumber) {
				return 1;
			} else if (bIsNumber) {
				//"Numeric identifiers always have lower precedence than non-numeric identifiers"
				return -1;
			} else {
				//"identifiers with letters or hyphens are compared lexically in ASCII sort order"
			}
		}
		if (aPreTokens.length != bPreTokens.length)
			return aPreTokens.length - bPreTokens.length;
		return 0;//they are equal
	}
	
	/**
	 * Major version number. Effectively final
	 */
	protected int major;
	/**
	 * Minor version number. Effectively final
	 */
	protected int minor;
	/**
	 * Patch number. Effectively final
	 */
	protected int patch;
	/**
	 * Prerelease string. Effectively final
	 */
	protected String prerelease;
	/**
	 * Metadata string. Effectively final
	 */
	protected String meta;
	/**
	 * 'cached' hash value
	 */
	protected int hash;
	/**
	 * Parses version string s
	 * 
	 * @param s
	 *            version string to parse
	 */
	public Version(String s) {
		Matcher m = semanticVersion.matcher(s);
		if (!m.find())
			throw new IllegalArgumentException("Input was not a valid version string");
		this.major = Integer.parseInt(m.group("major"));
		this.minor = Integer.parseInt(m.group("minor"));
		this.patch = Integer.parseInt(m.group("patch"));
		String prerel = m.group("prerel");
		this.prerelease = (prerel != null) ? prerel : "";
		String meta = m.group("meta");
		this.meta = (meta != null) ? meta : "";
	}
	
	/**
	 * 
	 * @param major
	 *            major version number
	 * @param minor
	 *            minor version number
	 * @param patch
	 *            patch number
	 */
	public Version(int major, int minor, int patch) {
		this(major, minor, patch, null, null);
	}
	
	/**
	 * 
	 * @param major
	 *            major version number
	 * @param minor
	 *            minor version number
	 * @param patch
	 *            patch number
	 * @param prerelease
	 *            prerelease string. Set to null for no prerelease string
	 */
	public Version(int major, int minor, int patch, String prerelease) {
		this(major, minor, patch, prerelease, null);
	}
	
	/**
	 * 
	 * @param major
	 *            major version number
	 * @param minor
	 *            minor version number
	 * @param patch
	 *            patch number
	 * @param prerelease
	 *            prerelease string. Set to null for no prerelease string
	 * @param meta
	 *            metadata string. Set to null for no metadata string
	 */
	public Version(int major, int minor, int patch, String prerelease, String meta) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.prerelease = (prerelease != null) ? prerelease : "";
		this.meta = (meta != null) ? meta : "";
	}
	
	/**
	 * Get major version number
	 * @return major version number
	 */
	public int getMajor() {
		return major;
	}
	
	/**
	 * Get minor version number
	 * @return minor version number
	 */
	public int getMinor() {
		return minor;
	}
	
	/**
	 * Get patch number
	 * @return patch number
	 */
	public int getPatch() {
		return patch;
	}
	
	/**
	 * Get prerelease string
	 * @return prerelease string, or empty string if not set
	 */
	public String getPrerelease() {
		return this.prerelease;
	}
	
	/**
	 * Get metadata string
	 * @return metadata string, or empty string if not set
	 */
	public String getMeta() {
		return this.meta;
	}
	
	/**
	 * Compares this version to another version with the method described in <a href="http://semver.org/#spec-item-11">semver.org � 11</a>.
	 * Amplitudes may not be correct when comparing versions, but signs are. A return value of 0 means that {@link #equals(Object)
	 * this.equals(other)} will return true.
	 * 
	 * @param other
	 *            other version to compare to
	 */
	@Override
	public int compareTo(Version other) {
		// Precedence MUST be calculated by separating the version into major, minor, patch, pre-release, and build identifiers in that order
		// Major, minor, and patch versions are always compared numerically.
		if (this.getMajor() != other.getMajor())
			return this.getMajor() - other.getMajor();
		if (this.getMinor() != other.getMinor())
			return this.getMinor() - other.getMinor();
		if (this.getPatch() != other.getPatch())
			return this.getPatch() - other.getPatch();
		/*
		 * Pre-release and build version precedence MUST be determined by comparing each
		 * dot separated identifier as follows: identifiers consisting of only digits are
		 * compared numerically and identifiers with letters or dashes are compared
		 * lexically in ASCII sort order. Numeric identifiers always have lower
		 * precedence than non-numeric identifiers.
		 */
		String myPreRel = getPrerelease();
		String otherPreRel = other.getPrerelease();
		if (!myPreRel.equals(otherPreRel)) {
			//prereleases "have a lower precedence than the associated normal version"
			if (myPreRel.isEmpty())
				return -1;
			if (otherPreRel.isEmpty())
				return 1;
			int cmp = compareStr(myPreRel, otherPreRel);
			if (cmp != 0)
				return cmp;
		}
		String myMeta = getMeta();
		String otherMeta = other.getMeta();
		if (!myMeta.equals(otherMeta)) {
			int cmp = compareStr(myMeta, otherMeta);
			if (cmp != 0)
				return cmp;
		}
		return 0;
	}
	
	/**
	 * Converts the version to a string, in the format of <code>major.minor.patch[-prerelease][+metadata]</code>.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getMajor());
		sb.append(".");
		sb.append(getMinor());
		sb.append(".");
		sb.append(getPatch());
		String prerel = getPrerelease();
		if (!(prerel == null || prerel.isEmpty())) {
			sb.append("-");
			sb.append(prerel);
		}
		String meta = getMeta();
		if (!(meta == null || meta.isEmpty())) {
			sb.append("+");
			sb.append(meta);
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other == this)
			return true;
		if (other instanceof Version) {
			Version otherVersion = (Version) other;
			return otherVersion.getMajor() == this.major
				&& otherVersion.getMinor() == this.minor
				&& otherVersion.getPatch() == this.patch
				&& (otherVersion.getMeta() == this.meta || (this.meta != null && this.meta.equals(otherVersion.getMeta())))
				&& (this.prerelease == otherVersion.getPrerelease() || (this.prerelease != null && this.prerelease.equals(otherVersion.getPrerelease())));
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		//TODO fix for efficiency and/or collision
		return (getMajor() << 8) ^ (getMinor() << 4) ^ (getPatch()) ^ (meta == null ? 0 : meta.hashCode()) ^ (prerelease == null ? 0 : prerelease.hashCode());
	}
}

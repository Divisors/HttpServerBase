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
	public static final Predicate<String> isInteger = Pattern.compile("[0-9]+").asPredicate();
	public static final Pattern semanticVersion = Pattern.compile("^(?<major>\\d+)\\.(?<minor>\\d+)\\.(?<patch>\\d+)(\\.\\d+)*(-(?<prerel>[0-9A-Za-z\\-\\.]+))?(\\+(?<meta>[0-9A-Za-z\\-\\.]+))?$");
	public static final int compareStr(String a, String b) {
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
				int aNumber = Integer.parseInt(aPreToken);
				int bNumber = Integer.parseInt(bPreToken);
				if (aNumber != bNumber)
					return aNumber - bNumber;
			} else if (aIsNumber) {
				return 1;
			} else if (bIsNumber) {
				return -1;
			} else {
				return aPreToken.compareTo(bPreToken);
			}
		}
		if (aPreTokens.length != bPreTokens.length)
			return aPreTokens.length - bPreTokens.length;
		return 0;
	}
	protected final int major;
	protected final int minor;
	protected final int patch;
	protected final String prerelease;
	protected final String meta;
	/**
	 * Parses version string s
	 * @param s version string to parse
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
	public Version(int major, int minor, int patch) {
		this(major, minor, patch, null, null);
	}
	public Version(int major, int minor, int patch, String prerelease) {
		this(major, minor, patch, prerelease, null);
	}
	public Version(int major, int minor, int patch, String prerelease, String meta) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.prerelease = (prerelease != null) ? prerelease : "";
		this.meta = (meta != null) ? meta : "";
	}
	public int getMajor() {
		return major;
	}
	public int getMinor() {
		return minor;
	}
	public int getPatch() {
		return patch;
	}
	public String getPrerelease() {
		return this.prerelease;
	}
	public String getMeta() {
		return this.meta;
	}
	@Override
	public int compareTo(Version other) {
		//Precedence MUST be calculated by separating the version into major, minor, patch, pre-release, and build identifiers in that order
		//Major, minor, and patch versions are always compared numerically.
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
}

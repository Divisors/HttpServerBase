package com.divisors.projectcuttlefish.httpserver.api;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.StandardCharsets;
import java.util.Formattable;
import java.util.Formatter;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONString;

/**
 * Simple implementation of semantic versioning.
 * 
 * @see {@link semver.org}
 * @author mailmindlin
 */
@NeedsTesting(since="caee2cb822fc17119ffc9ff7219d0890a42c548b")
public class Version implements Comparable<Version>, Externalizable, Formattable, JSONString {
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
	 * Compares strings in the method described by <a href="http://semver.org/#spec-item-11">semver.org § 11</a>.
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
	 * @return their comparison, which will be 1, 0, or -1 only
	 */
	@NeedsTesting
	public static final int compareStrings(String a, String b) {
		if (a.equals(b))
			return 0;
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
					return aNumber > bNumber ? 1 : -1;
			} else if (aIsNumber) {
				//"Numeric identifiers always have lower precedence than non-numeric identifiers"
				return -1;
			} else if (bIsNumber) {
				return 1;
			} else {
				//"identifiers with letters or hyphens are compared lexically in ASCII sort order"
				return aPreToken.compareTo(bPreToken) > 0 ? 1 : -1;
			}
		}
		if (aPreTokens.length != bPreTokens.length)
			return aPreTokens.length > bPreTokens.length ? 1 : -1;
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
	 * Stub for deserializing. Don't actually use this.
	 */
	protected Version() {
	
	}
	
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
		this.prerelease = (prerel != null && prerel.isEmpty()) ? null : prerel;
		String meta = m.group("meta");
		this.meta = (meta != null && meta.isEmpty()) ? null : meta;
		validate();
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
		this.prerelease = (prerelease != null && prerelease.isEmpty()) ? null : prerelease;
		this.meta = (meta != null && meta.isEmpty()) ? null : meta;
		validate();
	}
	
	@NeedsTesting
	protected void validate() {
		if (major < 0 || minor < 0 || patch < 0)
			//TODO customize error messages for each variable?
			throw new IllegalArgumentException("Major, minor, and patch numbers must be positive");
		if (isPrerelease()) {
			String[] identifiers = prerelease.split("\\.");
			int i = 0;
			for (String identifier : identifiers) {
				if (identifier.isEmpty()) {
					throw new IllegalArgumentException(String.format("Invalid prerelease identifier \"%s\" (#%d): identifiers MUST NOT be empty", identifier, i));
				}else if (isInteger.test(identifier)) {
					if (identifier.startsWith("0"))
						throw new IllegalArgumentException(String.format("Invalid prerelease identifier \"%s\" (#%d): numeric identifiers must not include leading zeroes", identifier, i));
				} else if(!asciiAlphaNumericTest.test(identifier)) {
					throw new IllegalArgumentException(String.format("Invalid prerelease identifier \"%s\" (#%d): identifiers MUST comprise only [0-9A-Za-z-]", identifier, i));
				}
				i++;
			}
		}
		if (hasMetadata()) {
			String[] identifiers = meta.split("\\.");
			int i = 0;
			for (String identifier : identifiers) {
				if (identifier.isEmpty()) {
					throw new IllegalArgumentException(String.format("Invalid metadata identifier \"%s\" (#%d): identifiers MUST NOT be empty", identifier, i));
				} else if(!asciiAlphaNumericTest.test(identifier)) {
					throw new IllegalArgumentException(String.format("Invalid metadata identifier \"%s\" (#%d): identifiers MUST comprise only [0-9A-Za-z-]", identifier, i));
				}
				i++;
			}
		}
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
	 * Determines if this version is a prerelease. A version is a prerelease
	 * if the prerelease string is not null or empty.
	 * @return whether this is a prerelease version
	 */
	public boolean isPrerelease() {
		return !this.getPrerelease().isEmpty();
	}
	
	/**
	 * Get prerelease string
	 * @return prerelease string, or empty string if not set
	 */
	public String getPrerelease() {
		return this.prerelease == null ? "" : this.prerelease;
	}
	
	/**
	 * Determines if this version has metadata. A version has metadata
	 * if its metadata string is not null or empty.
	 * @return whether this has any metadata
	 */
	public boolean hasMetadata() {
		return !this.getMeta().isEmpty();
	}
	
	/**
	 * Get metadata string
	 * @return metadata string, or empty string if not set
	 */
	public String getMeta() {
		return this.meta == null ? "" : this.meta;
	}
	
	/**
	 * Compares this version to another version with the method described in <a href="http://semver.org/#spec-item-11">semver.org § 11</a>.
	 * Amplitudes may not be correct when comparing versions, but signs are. A return value of 0 means that {@link #equals(Object)
	 * this.equals(other)} will return true.
	 * 
	 * @param other
	 *            other version to compare to
	 */
	@Override
	@NeedsTesting
	public int compareTo(Version other) {
		// Precedence MUST be calculated by separating the version into major, minor, patch, pre-release, and build identifiers in that order
		// Major, minor, and patch versions are always compared numerically.
		if (this.getMajor() != other.getMajor())
			return this.getMajor() - other.getMajor();
		if (this.getMinor() != other.getMinor())
			return this.getMinor() - other.getMinor();
		if (this.getPatch() != other.getPatch())
			return this.getPatch() - other.getPatch();
		
		boolean iAmPrerelease = this.isPrerelease();
		boolean otherIsPrerelease = other.isPrerelease();
		if (iAmPrerelease ^ otherIsPrerelease) {
			//either I am a prerelease version, or the other one is.
			//"Pre-release versions have a lower precedence than the associated normal version"
			return iAmPrerelease ? -1 : 1;
		} else if (iAmPrerelease && otherIsPrerelease) {
			int comparison = compareStrings(this.getPrerelease(), other.getPrerelease());
			if (comparison != 0)
				return comparison;
		}
		/*
		 * Build metadata SHOULD be ignored when determining version precedence.
		 * Thus two versions that differ only in the build metadata, have the same precedence.
		 */
		return 0;
	}
	
	/**
	 * Converts the version to a string, in the format of <code>major.minor.patch[-prerelease][+metadata]</code>.
	 */
	@Override
	@NeedsTesting
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getMajor());
		sb.append(".");
		sb.append(getMinor());
		sb.append(".");
		sb.append(getPatch());
		if (isPrerelease()) {
			sb.append("-");
			sb.append(getPrerelease());
		}
		if (hasMetadata()) {
			sb.append("+");
			sb.append(getMeta());
		}
		return sb.toString();
	}
	
	@Override
	@NeedsTesting
	public boolean equals(final Object other) {
		if (other == this)
			return true;
		if (other instanceof Version) {
			Version otherVersion = (Version) other;
			return otherVersion.getMajor() == this.getMajor()
					&& otherVersion.getMinor() == this.getMinor()
					&& otherVersion.getPatch() == this.getPatch()
					&& this.getPrerelease().equals(otherVersion.getPrerelease())
					&& this.getMeta().equals(otherVersion.getMeta());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO fix for efficiency and/or collision
		int h = this.hash;
		if (h == 0) {
			h = (getMajor() << 16) ^ (getMinor() << 8) ^ (getPatch()) + (meta == null ? 0 : meta.hashCode())
					^ (prerelease == null ? 0 : prerelease.hashCode());
			this.hash = h;
		}
		return h;
	}
	
	@Override
	@NeedsTesting
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(0xDEADBAAD);// for sanity check
		out.writeInt(this.major);
		out.writeInt(this.minor);
		out.writeInt(this.patch);
		
		if (isPrerelease()) {
			byte[] data = this.getPrerelease().getBytes(StandardCharsets.UTF_8);
			out.writeInt(data.length);
			out.write(data);
		} else {
			out.writeInt(0);
		}
		
		if (hasMetadata()) {
			byte[] data = this.getMeta().getBytes(StandardCharsets.UTF_8);
			out.writeInt(data.length);
			out.write(data);
		} else {
			out.writeInt(0);
		}
	}
	
	@Override
	@NeedsTesting
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// perform sanity check
		if (in.readInt() != 0xDEADBAAD)
			throw new IOException("Sanity check when deserializing failed");
			
		this.major = in.readInt();
		this.minor = in.readInt();
		this.patch = in.readInt();
		
		int len;
		if ((len = in.readInt()) > 0) {
			byte[] buf = new byte[len];
			in.read(buf);
			this.prerelease = new String(buf, StandardCharsets.UTF_8);
		}
		if ((len = in.readInt()) > 0) {
			byte[] buf = new byte[len];
			in.read(buf);
			this.meta = new String(buf, StandardCharsets.UTF_8);
		}
		
		validate();
	}

	/**
	 * Essentially {@link #toString()} surrounded with quotes, for storing as JSON.
	 */
	@Override
	@NeedsTesting
	public String toJSONString() {
		return '"' + this.toString() + "'";
	}

	@Override
	public void formatTo(Formatter formatter, int flags, int width, int precision) {
		// TODO Auto-generated method stub
		
	}
}

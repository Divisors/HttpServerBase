package com.divisors.projectcuttlefish.contentmanager.api.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Version;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpContext;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public class StaticFileResource implements Resource {
	protected final Path path;
	protected final ResourceTag tag;
	protected String etagStrong, etagWeak;
	protected FileTime lastModified;

	public StaticFileResource(File f, Version version) {
		this(f.toPath(), version);
	}

	public StaticFileResource(Path p, Version version) {
		this(p, p.getFileName().toString(), version);
	}

	public StaticFileResource(Path p, String name, Version version) {
		this.path = p;
		this.tag = new ResourceTag(name, version);
		try {
			this.lastModified = Files.getLastModifiedTime(this.path);
			this.etagStrong = this.generateEtagStrong();
		} catch (IOException e) {
			System.err.println(p.toString() + " does not exist");
			this.lastModified = null;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long estimateSize() {
		try {
			return Files.size(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public ResourceTag getTag() {
		return tag;
	}

	@Override
	public String getEtag(boolean strong) throws RuntimeException {
		if (strong) {
			if (this.etagStrong != null)
				return this.etagStrong;

			try {
				this.etagStrong = generateEtagStrong();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			return this.etagStrong;
		} else {
			if (this.etagWeak != null)
				return this.etagWeak;
			try {
				etagWeak = generateEtagWeak();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
			return etagWeak;
		}
	}

	protected String generateEtagStrong() throws NoSuchAlgorithmException, IOException {
		// calculate SHA-1 hash of the file, and return it in base64
		// Note that this is not
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		try (InputStream is = Files.newInputStream(this.path, StandardOpenOption.READ)) {
			final byte[] buffer = new byte[1024];
			for (int read = 0; (read = is.read(buffer)) != -1;) {
				messageDigest.update(buffer, 0, read);
			}
		}

		// Convert the byte to hex format
		try (Formatter formatter = new Formatter()) {
			for (final byte b : messageDigest.digest())
				formatter.format("%02x", b);
			return formatter.toString();
		}
	}

	protected String generateEtagWeak() throws NoSuchAlgorithmException {
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
		messageDigest.update(new StringBuilder(tag.name).append(':').append(tag.getVersion().getMajor()).append(".")
				.append(tag.getVersion().getMinor())
				// Don't use the patch; these are assumed to be equivalent
				.toString().getBytes());

		// Convert the byte to hex format
		try (Formatter formatter = new Formatter()) {
			for (final byte b : messageDigest.digest())
				formatter.format("%02x", b);
			return formatter.toString();
		}
	}

	@Override
	public HttpResponsePayload getPayload(HttpRequest request, HttpContext context) {
		try {
			return new HttpFileResponsePayload();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Payload wrapping a FileResource
	 * 
	 * @author mailmindlin
	 */
	public class HttpFileResponsePayload implements HttpResponsePayload {
		public static final int BUFFER_SIZE = 4096;
		InputStream is;
		volatile boolean open = true;

		public HttpFileResponsePayload() throws IOException {
			is = Files.newInputStream(path);
		}

		@Override
		public long remaining() {
			if (is == null)
				return -1;
			try {
				return is.available();
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
		}

		@Override
		public void drainTo(Consumer<ByteBuffer> writer) {
			byte[] buffer = new byte[BUFFER_SIZE];
			int len;
			try {
				while ((len = is.read(buffer)) > 0) {
					writer.accept(ByteBuffer.wrap(buffer, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isDone() {
			if (remaining() <= 0)
				open = false;
			return open;
		}

		@Override
		public void close() {
			open = false;
			try {
				if (is != null)
					is.close();
				is = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}

package com.divisors.projectcuttlefish.contentmanager.api.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Version;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;

public class FileResource implements Resource {
	protected final Path path;
	protected final ResourceTag tag;
	protected String etag;
	public FileResource(File f, Version version) {
		this(f.toPath(), version);
	}
	public FileResource(Path p, Version version) {
		this.path = p;
		this.tag = new ResourceTag(p.getFileName().toString(), version);
	}
	public FileResource(Path p, String name, Version version) {
		this.path = p;
		this.tag = new ResourceTag(name, version);
	}
	@Override
	public String getName() {
		return getTag().name;
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
	public String getEtag() {
		if (etag == null)
			etag = new StringBuilder(tag.name)
					.append(':')
					.append(tag.getVersion())
					.append(':')
					.append("")
					.toString();
		return etag;
	}
	@Override
	public HttpResponsePayload toPayload() {
		try {
			return new HttpFileResponsePayload();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
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

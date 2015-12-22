package com.divisors.projectcuttlefish.httpserver.util;

import java.nio.ByteBuffer;

public class ByteParsingUtils {
	public static final class ByteBufferTokenizer {
		public static final int DEFAULT_CAPACITY = 8196;
		protected final byte[] token;
		protected final ByteBuffer buffer;
		protected int offset = 0;
		public ByteBufferTokenizer(byte[] token) {
			this(token, ByteBuffer.allocate(DEFAULT_CAPACITY));
		}
		public ByteBufferTokenizer(byte[] token, int capacity) {
			this(token, ByteBuffer.allocate(capacity));
		}
		public ByteBufferTokenizer(byte[] token, ByteBuffer buffer) {
			this.token = token;
			this.buffer = buffer;
		}
		public int remaining() {
			return -1;//TODO finish
		}
		public int available() {
			return buffer.remaining();
		}
		public ByteBuffer getBuffer() {
			return this.buffer;
		}
		public boolean put(byte...bytes) {
			try {
				buffer.put(bytes);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		public boolean put(int...bytes) {
			byte[] arr = new byte[bytes.length];
			for (int i=0;i<bytes.length;i++)
				arr[i]=(byte)bytes[i];
			return this.put(arr);
		}
		public boolean put(ByteBuffer buf) {
			try {
				buffer.put(buf);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		public ByteBufferTokenizer clear() {
			synchronized (buffer) {
				this.buffer.clear();
			}
			return this;
		}
		/**
		 * ByteBuffer containing segment of next token parsed.
		 * @return
		 */
		public ByteBuffer next() {
			synchronized (this.buffer) {
				System.out.println("Offset " + offset);
				ByteBuffer mirror = buffer.duplicate();
				mirror.limit(mirror.position()).position(offset);
				//TODO optimize for speed
				search:
				while (mirror.remaining() >= token.length) {
					byte b = mirror.get();
					if (b == token[0]) {
						if (token.length > 1) {
							mirror.mark();
							byte[] minibuf = new byte[token.length - 1];
							mirror.get(minibuf);
							for (int i=0; i < minibuf.length; i++)
								if (this.token[i+1] != minibuf[i]) {
									mirror.reset();
									continue search;
								}
						}
						int size = mirror.position() - offset;
						mirror.position(offset).limit(offset + size);
						offset += size;
						return mirror;
					}
				}
			}
			return null;
		}
		/**
		 * Return a buffer containing whatever bytes have not yet been tokenized.
		 */
		public ByteBuffer scraps() {
			System.out.println("Offset: "+offset);
			ByteBuffer mirror = buffer.duplicate().asReadOnlyBuffer();
			mirror.position(offset).limit(buffer.position());
			return mirror;
		}
	}
}

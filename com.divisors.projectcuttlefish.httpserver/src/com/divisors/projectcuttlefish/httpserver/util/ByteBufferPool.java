package com.divisors.projectcuttlefish.httpserver.util;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

public abstract class ByteBufferPool {
	/**
	 * ByteBuffers will be generated at exactly the capacity that they are needed. Use this
	 * option if you are going to use 
	 * @param direct
	 * @param maxSize
	 * @return
	 */
	public static ByteBufferPool minimal(boolean direct, int maxSize) {
		return new ExactByteBufferPool(direct, maxSize);
	}
	/**
	 * ByteBuffers will be generated 
	 * @param direct
	 * @param maxSize
	 * @param blockSize
	 * @return
	 */
	public static ByteBufferPool linear(int blockSize, boolean direct, int maxSize) {
		return new SteppedByteBufferPool(blockSize, direct, maxSize);
	}
	public static ByteBufferPool exponential(double base, boolean direct, int maxSize) {
		return new ExponentialByteBufferPool(base, direct, maxSize);
	}
	protected final boolean direct;
	protected final int maxSize;
	protected final AtomicInteger size = new AtomicInteger(0);
	ConcurrentHashMap<Integer, ByteBufferWrapper> buffers = new ConcurrentHashMap<>();
	ConcurrentHashMap<Integer, Set<ByteBufferWrapper>> buffersAvailable = new ConcurrentHashMap<>();
	public ByteBufferPool(boolean direct, int maxSize) {
		this.direct = direct;
		this.maxSize = maxSize;
	}
	public ByteBufferWrapper get(int size) {
		Set<ByteBufferWrapper> wrappers = buffersAvailable.get(getCapacityFor(size));
		if (wrappers != null) {
			synchronized (wrappers) {
				for (ByteBufferWrapper wrapper : wrappers)
					if ((!wrapper.isLocked()) && wrapper.isAllocated() && wrappers.remove(wrapper))
						return wrapper.lock();
			}
		}
		return allocate(size).lock();
	}
	protected ByteBufferWrapper allocate(int size) {
		System.out.println("Allocating new buffer: " + size);
		ByteBuffer buffer;
		if (direct) {
			buffer = ByteBuffer.allocateDirect(size);
		} else {
			buffer = ByteBuffer.allocate(size);
		}
		ByteBufferWrapper result = new ByteBufferWrapper(this, buffer);
		buffers.put(buffer.hashCode(), result);
		return result;
	}
	public void recycle(ByteBufferWrapper wrapper) {
		int blocks = getCapacityFor(wrapper.get().capacity());
		wrapper.unlock();
		buffersAvailable.computeIfAbsent(blocks, (i)->(new HashSet<ByteBufferWrapper>())).add(wrapper);
	}
	protected void release(ByteBufferWrapper wrapper) {
		//TODO finish
		throw new UnsupportedOperationException();
	}
	/**
	 * Defines what size a buffer should have. Its result should be >= the input, or problems
	 * will occur.
	 */
	protected abstract int getCapacityFor(int size);
	
	public void destroy() {
		for (ByteBufferWrapper buffer : buffers.values())
			buffer.destroy();
		buffers.clear();
		buffersAvailable.clear();
		buffers = null;
		buffersAvailable = null;
	}
	
	public static class ByteBufferWrapper {
		final SoftReference<ByteBuffer> buffer;
		final ByteBufferPool pool;
		final AtomicBoolean lock = new AtomicBoolean(false);
		public ByteBufferWrapper(ByteBufferPool pool, ByteBuffer buffer) {
			this.pool = pool;
			this.buffer = new SoftReference<>(buffer);
		}
		public ByteBuffer get() {
			return buffer.get();
		}
		public ByteBuffer getReference() {
			return buffer.get();
		}
		public boolean isLocked() {
			return lock.get();
		}
		public boolean isAllocated() {
			return buffer.get() != null;
		}
		public ByteBufferWrapper lock() {
			if (!lock.compareAndSet(false, true))
				throw new IllegalStateException("Buffer is locked");
			return this;
		}
		public void unlock() {
			if (!lock.compareAndSet(true, false))
				throw new IllegalStateException("Buffer was not locked");
		}
		public void recycle() {
			pool.recycle(this);
		}
		public void destroy() {
			if (buffer.isEnqueued())
				return;
			{
				ByteBuffer buf = buffer.get();
				if (buf != null && buf.isDirect()) {
					try {
						Cleaner cleaner = ((DirectBuffer) buf).cleaner();
						if (cleaner != null)
							cleaner.clean();
					} catch (ClassCastException | NullPointerException | SecurityException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
			buffer.enqueue();
		}
	}
	public static class ExactByteBufferPool extends ByteBufferPool {
		public ExactByteBufferPool(boolean direct, int maxSize) {
			super(direct, maxSize);
		}
		@Override
		protected int getCapacityFor(int size) {
			return size;
		}
	}
	public static class SteppedByteBufferPool extends ByteBufferPool {
		protected final int blockSize;
		public SteppedByteBufferPool(int blockSize, boolean direct, int maxSize) {
			super(direct, maxSize);
			this.blockSize = blockSize;
		}
		@Override
		protected int getCapacityFor(int size) {
			//first multiple of blockSize >= size
			return ((size + blockSize - 1) / blockSize) * blockSize;
		}
	}
	public static class ExponentialByteBufferPool extends ByteBufferPool {
		final double base, logB;
		public ExponentialByteBufferPool(double base, boolean direct, int maxSize) {
			super(direct, maxSize);
			this.base = base;
			this.logB = Math.log(base);
		}
		@Override
		protected int getCapacityFor(int size) {
			return (int)Math.ceil(Math.pow(base, Math.ceil(Math.log(size)/logB)));
		}
	}
}

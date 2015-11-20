package com.divisors.projectcuttlefish.httpserver.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection;

/**
 * Implementation of {@linkplain com.divisors.projectcuttlefish.httpserver.api.tcp.TcpConnection}
 * @author mailmindlin
 */
public class TcpConnectionImpl implements TcpConnection {
	protected BlockingQueue<byte[]> inputBuffer, outputBuffer;
	protected final SocketChannel channel;
	protected AtomicLong id = new AtomicLong(-1);
	protected boolean isEOS = false;
	public TcpConnectionImpl(SocketChannel channel) {
		this.channel = channel;
		this.inputBuffer = new LinkedBlockingQueue<>();
		this.outputBuffer = new LinkedBlockingQueue<>();
	}
	public TcpConnectionImpl(SocketChannel channel, BlockingQueue<byte[]> inputQueue, BlockingQueue<byte[]> outputQueue) {
		this.channel = channel;
		this.inputBuffer = inputQueue;
		this.outputBuffer = outputQueue;
	}
	@Override
	public void put(byte[] e) throws InterruptedException {
		outputBuffer.put(e);
	}

	@Override
	public boolean offer(byte[] e, long timeout, TimeUnit unit) throws InterruptedException {
		return outputBuffer.offer(e, timeout, unit);
	}

	@Override
	public byte[] poll(long timeout, TimeUnit unit) throws InterruptedException {
		return inputBuffer.poll(timeout, unit);
	}

	@Override
	public int drainTo(Collection<? super byte[]> c, int maxElements) {
		return inputBuffer.drainTo(c, maxElements);
	}

	@Override
	public byte[] poll() {
		return inputBuffer.poll();
	}

	@Override
	public byte[] element() {
		return inputBuffer.element();
	}

	@Override
	public byte[] peek() {
		return inputBuffer.peek();
	}

	@Override
	public int size() {
		return inputBuffer.size();
	}

	@Override
	public boolean isEmpty() {
		return inputBuffer.isEmpty();
	}

	@Override
	public Iterator<byte[]> iterator() {
		return inputBuffer.iterator();
	}

	@Override
	public Object[] toArray() {
		return inputBuffer.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return inputBuffer.toArray(a);
	}

	@Override
	public boolean addAll(Collection<? extends byte[]> c) {
		return outputBuffer.addAll(c);
	}

	@Override
	public void clear() {
		inputBuffer.clear();
		outputBuffer.clear();
	}

	@Override
	public boolean add(byte[] data) throws IllegalStateException {
		return outputBuffer.add(data);
	}

	@Override
	public int drainTo(Collection<? super byte[]> c) {
		return inputBuffer.drainTo(c);
	}

	@Override
	public boolean offer(byte[] data) {
		return outputBuffer.offer(data);
	}

	@Override
	public int remainingCapacity() {
		return outputBuffer.remainingCapacity();
	}

	@Override
	public byte[] take() throws InterruptedException {
		return inputBuffer.take();
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void close() throws IOException {
		this.channel.close();
	}
	
	@Override
	public long getConnectionID() {
		return id.get();
	}
	
	@Override
	public void setConnectionID(long id) {
		this.id.set(id);
	}
	
	@Override
	public int read(ByteBuffer buffer) throws IOException {
		int read, totalRead = -1;
		
		while ((read = this.channel.read(buffer)) > 0)
			totalRead += read;

		if (read == -1)
			isEOS = true;

		return totalRead;
	}

	@Override
    public int write(ByteBuffer buffer) throws IOException{
        int written = this.channel.write(buffer);
        int totalBytesWritten = written;

        while(written > 0 && buffer.hasRemaining()){
        	written = this.channel.write(buffer);
            totalBytesWritten += written;
        }

        return totalBytesWritten;
    }
	
    public InputStream getInputStream() {
    	return new InputStream() {
    		@Override
    		public int read(byte[] array, int off, int len) throws IOException {
    			return TcpConnectionImpl.this.read(ByteBuffer.wrap(array, off, len));
    		}
			@Override
			public int read() throws IOException {
				byte[] result = new byte[1];
				if (read(result, 1, 0) > 0)
					return result[0];
				throw new IOException("Socket closed");
			}
    	};
    }
    public OutputStream getOutputStream() {
    	return new OutputStream() {
    		@Override
    		public void write(byte[] array, int off, int len) throws IOException {
    			TcpConnectionImpl.this.write(ByteBuffer.wrap(array, off, len));
    		}
			@Override
			public void write(int b) throws IOException {
				TcpConnectionImpl.this.write(ByteBuffer.wrap(new byte[]{(byte)b}));
			}
    	};
    }
	@Override
	public long readNext(ByteBuffer buffer) throws IOException {
		buffer.clear();
		int read = read(buffer);
		buffer.flip();
		
		this.inputBuffer.add(buffer.array());
		
		return read;
	}
	@Override
	public long writeNext(ByteBuffer buffer) throws IOException {
		buffer.clear();
		byte[] toWrite = this.outputBuffer.poll();
		if (toWrite == null)
			return 0;
		if (toWrite.length > buffer.remaining()) {
			int written = 0;
			int chunk = buffer.limit();
			while (written < toWrite.length) {
				buffer.clear();
				buffer.put(toWrite, written, chunk);
				written += write(buffer);
			}
			return written;
		}
		buffer.put(toWrite);
		byte[] peek;
		while ((peek = outputBuffer.peek()) != null && peek.length < buffer.remaining())
			buffer.put(outputBuffer.poll());
		System.out.println("Writing " + buffer.position() + " bytes");
		return write(buffer);
	}
}

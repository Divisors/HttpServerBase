package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.divisors.projectcuttlefish.httpserver.api.Channel;

import reactor.io.buffer.Buffer;

/**
 * A connection between a server and a client. Methods are (mostly) non-blocking,
 * and queue-based.
 * TODO add prioritization for messages
 * @author mailmindlin
 * 
 * @see com.projectcuttlefish.httpserver.impl.ConnectionImpl
 *
 */
public interface TcpChannel extends AutoCloseable, Channel<Buffer, Buffer> {
	/**
	 * Add byte[] to output buffer
	 * @param data message to add
	 * @return if operation worked
	 * @throws IllegalStateException if the connection is closed
	 */
	boolean add(byte[] data) throws IllegalStateException;
	boolean isOpen();
	
	long getConnectionID();
	void setConnectionID(long id);
	int write(ByteBuffer buffer) throws IOException;
	int read(ByteBuffer buffer) throws IOException;

	/**
	 * Read the next set of data from the channel, and store it in the input buffer.
	 * Note that the {@code buffer} passed as an argument cannot be safely used to get
	 * what data was read. If the ammount of data exceeds the size of the buffer,
	 * it may read multiple messages.
	 * @param buffer optional buffer for reading
	 * @return total number of bytes read, or -1 if closed
	 * @throws IOException if there was a problem with reading
	 */
	long readNext(ByteBuffer buffer) throws IOException;
	/**
	 * Write the next array of data from the queue to the channel. If the next array is larger
	 * than the passed buffer, then it may write multiple times.
	 * @param buffer to store the data
	 * @return total number of bytes written, or -1 if closed
	 * @throws IOException
	 */
	long writeNext(ByteBuffer buffer) throws IOException;
	@Override
	void close() throws IOException;
}

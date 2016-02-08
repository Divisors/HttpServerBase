package com.divisors.projectcuttlefish.crypto.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpChannelImpl;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

/**
 * 
 * @author mailmindlin
 *
 */
public class SSLChannelImpl extends TcpChannelImpl {
	private final SSLEngine engine;
	public SSLChannelImpl(SSLEngine engine, TcpServerImpl server, SocketChannel socket, final long id) {
		super(server, socket, id);
		this.engine = engine;
	}
	@Override
	protected int doWrite() throws IOException {
		System.out.println("\tWill write...");
		ByteBuffer buf;
		synchronized (this.writeQueue) {
			buf = writeQueue.poll();
			if (buf == null) {
				System.out.println("\tEmpty buffer (nothing to write)");
				return 0;
			}
			// If the buffer taken is really small, and there is another available, take it too.
			if (buf.remaining() < 1024 && !writeQueue.isEmpty()) {
				ByteBuffer buf2 = writeQueue.poll();
				if (buf2 != null) {
					ByteBuffer tmp = buf;
					buf = ByteBuffer.allocate(buf2.remaining() + tmp.remaining()).put(tmp).put(buf2);
					buf.flip();
				}
			}
			
			//write to socket
			int written = this.socket.write(buf);
			
			if (written < 0)
				return written;
			
			System.out.println("\tWrote " + written + "/" + (written + buf.remaining()) + " bytes to #" + this.getConnectionID());
			//not everything was written from the buffer, so queue it for writing next time
			if (buf.remaining() > 0)
				this.writeQueue.addFirst(buf);
			
			//'tell' the selector that this can write more
			if (!this.writeQueue.isEmpty()) {
				SelectionKey key = socket.keyFor(getSelector());
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
			return written;
		}
	}
	protected void wrap(ByteBuffer b) {
		SSLEngineResult result;
		try {
			b.flip();
			engine.wrap(b, null);
		} catch (Exception e) {
			
		}
	}
}

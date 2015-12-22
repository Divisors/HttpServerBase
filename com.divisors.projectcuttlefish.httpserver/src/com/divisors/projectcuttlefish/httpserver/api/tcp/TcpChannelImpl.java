package com.divisors.projectcuttlefish.httpserver.api.tcp;

import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.Codec;

import reactor.bus.Event;
import reactor.bus.registry.Registration;
import reactor.fn.Consumer;

/**
 * Implementation of {@link TcpChannel}. 
 * @author mailmindlin
 * @see TcpChannel
 */
public class TcpChannelImpl implements TcpChannel {
	/**
	 * Socket that this channel is overlaying
	 */
	protected final SocketChannel socket;
	/**
	 * Server that this channel is from
	 * @see #getServer()
	 */
	protected final TcpServerImpl server;
	/**
	 * Connection id
	 * @see #getConnectionID()
	 */
	protected final long connID;
	protected final List<Registration<Object,Consumer<? extends Event<?>>>> subscriptions = new ArrayList<>();
	protected final Deque<ByteBuffer> writeQueue = new LinkedList<>();//TODO use better queue
	/**
	 * Create channel from server, wrapping socket, with id.
	 * @param server parent server
	 * @param socket socket to wrap
	 * @param id connection id
	 */
	public TcpChannelImpl(TcpServerImpl server, SocketChannel socket, final long id) {
		this.server = server;
		this.socket = socket;
		this.connID = id;
	}

	@Override
	public boolean isOpen() {
		return this.socket.isOpen();
	}

	@Override
	public long getConnectionID() {
		return connID;
	}
	/**
	 * Close this channel, canceling any event listeners registered through this
	 */
	@Override
	public void close() throws IOException {
		//DEBUG
		try {
			StringBuilder sb = new StringBuilder("Closing tcp channel #")
				.append(this.getConnectionID());
				SocketAddress addr = getRemoteAddress();
			if (addr != null)
				sb.append(" connected to ")
					.append(addr);
			System.out.println(sb.toString());
		} catch (Exception e){}
		this.writeQueue.clear();
		this.subscriptions.forEach(Registration::cancel);
		this.socket.close();
	}

	@Override
	public TcpServerImpl getServer() {
		return server;
	}
	
	@Override
	public SocketAddress getRemoteAddress() {
		try {
			return socket.getRemoteAddress();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * TODO stackoverflow.com/questions/10940654/java-socketchannel-register-for-multiple-op-codes-is-never-selected#comment14277475_10940910
	 */
	@Override
	public TcpChannelImpl write(ByteBuffer data) {
		System.out.println("Queueing "+data.remaining() + " bytes for writing #"+this.getConnectionID() + this.isOpen());
		this.writeQueue.add(data);
		SelectionKey key = this.socket.keyFor(this.getServer().selector);
		key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
		System.out.println("" + key.isValid() + key.interestOps());
		//notify the selector of the interest
		this.getServer().selector.wakeup();//this decreased latency by like >5000% (really), because writes were being blocked until the another socket triggered an event
		return this;
	}
	@Override
	public TcpChannelImpl onRead(Consumer<ByteBuffer> handler) {
		this.subscriptions.add(server.bus.on($t("tcp.read",this.getConnectionID()),event->handler.accept((ByteBuffer)event.getData())));
		return this;
	}
	@Override
	public <X, Y> Channel<X, Y> map(Codec<ByteBuffer, ByteBuffer, X, Y> codec) {
		// TODO Auto-generated method stub
		return null;
	}
	protected int doWrite() throws IOException {
		System.out.println("Will write..");
		synchronized (this.writeQueue) {
			ByteBuffer buf = writeQueue.poll();
			if (buf == null)
				return 0;
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
			
			System.out.println("Wrote "+written + "/"+(written + buf.remaining()) + " bytes to #" + this.getConnectionID());
			//not everything was written from the buffer, so queue it for writing next time
			if (buf.remaining() > 0)
				this.writeQueue.addFirst(buf);
			
			//'tell' the selector that this can write more
			if (!this.writeQueue.isEmpty()) {
				SelectionKey key = socket.keyFor(getServer().selector);
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
			return written;
		}
	}
}

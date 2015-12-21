package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
	protected final SocketChannel socket;
	protected final TcpServerImpl server;
	protected final long connID;
	protected final List<Registration<Object,Consumer<? extends Event<?>>>> subscriptions = new ArrayList<>();
	protected final Queue<ByteBuffer> writeQueue = new LinkedList<>();//TODO use better queue
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
		this.socket.close();
		this.subscriptions.forEach(Registration::cancel);
	}

	@Override
	public TcpServer getServer() {
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
	@Override
	public TcpChannelImpl write(ByteBuffer data) {
		this.writeQueue.offer(data);
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

}

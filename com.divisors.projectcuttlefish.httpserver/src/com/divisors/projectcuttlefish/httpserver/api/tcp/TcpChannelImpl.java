package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.Codec;

public class TcpChannelImpl implements TcpChannel {
	protected final SocketChannel socket;
	protected final TcpServerImpl server;
	public TcpChannelImpl(TcpServerImpl server, SocketChannel socket) {
		this.server = server;
		this.socket = socket;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getConnectionID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setConnectionID(long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws IOException {
		this.socket.close();
	}

	@Override
	public TcpServer getServer() {
		return server;
	}
	
	@Override
	public SocketAddress getRemoteAddress() throws IOException {
		return socket.getRemoteAddress();
	}
	@Override
	public Channel<reactor.io.buffer.Buffer, reactor.io.buffer.Buffer> write(reactor.io.buffer.Buffer data) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Channel<reactor.io.buffer.Buffer, reactor.io.buffer.Buffer> onRead(
			Consumer<reactor.io.buffer.Buffer> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <X, Y> Channel<X, Y> map(Codec<reactor.io.buffer.Buffer, X, reactor.io.buffer.Buffer, Y> codec) {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.divisors.projectcuttlefish.httpserver.api.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.Codec;

import reactor.fn.Consumer;
import reactor.io.buffer.Buffer;

public class TcpChannelImpl implements TcpChannel {
	protected final SocketChannel socket;
	protected final TcpServerImpl server;
	public TcpChannelImpl(TcpServerImpl server, SocketChannel socket) {
		this.server = server;
		this.socket = socket;
	}
	@Override
	public Channel<Buffer, Buffer> write(Buffer data) {
		return null;
	}

	@Override
	public Channel<Buffer, Buffer> onRead(Consumer<Buffer> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X, Y> Channel<X, Y> map(Codec<Buffer, X, Buffer, Y> codec) {
		// TODO Auto-generated method stub
		return null;
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

}
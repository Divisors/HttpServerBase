package com.divisors.projectcuttlefish.httpserver.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.divisors.projectcuttlefish.httpserver.api.Action;
import com.divisors.projectcuttlefish.httpserver.api.Channel;
import com.divisors.projectcuttlefish.httpserver.api.ChannelOption;
import static com.divisors.projectcuttlefish.httpserver.api.tcp.SubsetSelector.$t;

import reactor.fn.Consumer;

public class TcpClientChannel implements Channel<ByteBuffer, ByteBuffer> {
	protected final TcpClient client;
	protected final SocketAddress addr;
	protected final SocketChannel socket;
	protected final long id;
	protected TcpClientChannel(TcpClient client, SocketAddress addr, long id) throws IOException {
		this.client = client;
		this.addr = addr;
		this.id = id;
		this.socket = SocketChannel.open();
		socket.configureBlocking(false);
	}
	@Override
	public Channel<ByteBuffer, ByteBuffer> write(ByteBuffer data) {
		// TODO Auto-generated method stub
		return null;
		System.out.println("TCPc::Queuing " + data.remaining() + " bytes for writing.");
	}
	@Override
	public Action onRead(Consumer<ByteBuffer> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public <E> Channel<ByteBuffer, ByteBuffer> setOption(ChannelOption<E> key, E value) {
		// TODO Auto-generated method stub
		return null;
	}
	public long getConnectionID() {
		return this.id;
	}
	public Action onConnect(Consumer<TcpClientChannel> handler) {
		client.bus.on($t("tcp.connect",getConnectionID()), null);//TODO finish
		return null;
	}
	public SocketAddress getRemoteAddress() {
		return addr;
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}
}

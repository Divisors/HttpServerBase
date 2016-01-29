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
	//TODO use better queue
	protected final Deque<ByteBuffer> writeQueue = new LinkedList<>();
	protected TcpClientChannel(TcpClient client, SocketAddress addr, long id) throws IOException {
		this.client = client;
		this.addr = addr;
		this.id = id;
		this.socket = SocketChannel.open();
		socket.configureBlocking(false);
	}
	@Override
	public TcpClientChannel write(ByteBuffer data) {
		System.out.println("TCPc::Queuing " + data.remaining() + " bytes for writing.");
		writeQueue.push(data);
		SelectionKey key = socket.keyFor(client.selector);
		key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);//TODO maybe queue also
		client.selector.wakeup();
		return this;
	}
	@Override
	@SuppressWarnings("unchecked")
	public Action onRead(Consumer<ByteBuffer> handler) {
		Registration<?,?> registration = client.bus.on($t("tcp.read",getConnectionID()), event->handler.accept(((Event<ByteBuffer>)event).getData()));
		return new RegistrationCancelAction(registration);
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
	@SuppressWarnings("unchecked")
	public Action onConnect(Consumer<TcpClientChannel> handler) {
		Registration<?,?> registration = client.bus.on($t("tcp.accept",getConnectionID()), event->handler.accept(((Event<TcpClientChannel>)event).getData()));
		registration.cancelAfterUse();// Connect should be called only once, so clean it up soon
		return new RegistrationCancelAction(registration);
	}
	public SocketAddress getRemoteAddress() {
		return addr;
	}
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
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
				SelectionKey key = socket.keyFor(client.selector);
				key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
			}
			return written;
		}
	}
}

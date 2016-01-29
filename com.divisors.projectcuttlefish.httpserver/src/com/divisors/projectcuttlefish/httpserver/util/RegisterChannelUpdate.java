package com.divisors.projectcuttlefish.httpserver.util;

import java.lang.ref.WeakReference;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

/**
 * Registers a channel with a selector, as a nicely queueable consumer.
 * @author mailmindlin
 *
 */
public class RegisterChannelUpdate implements Consumer<Selector> {
	protected final WeakReference<SocketChannel> channel;
	protected final int ops;
	protected Object attachment;
	public RegisterChannelUpdate(SocketChannel channel, int ops, Object attachment) {
		this.channel = new WeakReference<>(channel);
		this.ops = ops;
		this.attachment = attachment;
	}
	@Override
	public void accept(Selector selector) {
		SocketChannel channel = this.channel.get();
		if (channel != null)
			try {
				SelectionKey key = channel.register(selector, ops, attachment);
				key.attach(attachment);
			} catch (ClosedChannelException e) {
				e.printStackTrace();
			}
		this.channel.clear();
		this.attachment = null;
	}
}

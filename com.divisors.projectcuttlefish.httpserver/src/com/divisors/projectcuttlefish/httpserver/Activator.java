package com.divisors.projectcuttlefish.httpserver;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;

import reactor.core.processor.RingBufferProcessor;


public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServerFactoryServiceRegistration;
	ServiceRegistration<?> serverFactoryServiceRegistration;
	TcpServer tcp;
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			System.out.println("Initializing: ProjectCuttlefish|HttpServer");
			//just random testing -- binds to a random local port
			tcp = new TcpServerImpl(new InetSocketAddress("localhost", (int)(Math.random() * 10000) + 1000))
					.start(server-> {
						((TcpServerImpl)server)
							.dispatchOn(RingBufferProcessor.create("test1", 32))
							.runOn(Executors.newCachedThreadPool())
							.onConnect((channel)-> {
								try {
									System.out.println("Connected to channel @ " + channel.getRemoteAddress() + ", id=" + channel.getConnectionID());
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								channel.onRead(data->{
									//print out the data sent to this
									System.out.println("Recieved request from " + channel.getRemoteAddress());
									System.out.println(new String(data.array()));
									try {
										channel.close();
									} catch (Exception e) {
										e.printStackTrace();
									}
								});
							});
						//try to open the default browser to send a request to this server
						if (Desktop.isDesktopSupported())
							try {
								Desktop.getDesktop().browse(new URI("http://localhost:" + ((InetSocketAddress)server.getAddress()).getPort()));
							} catch (URISyntaxException | IOException e) {
								e.printStackTrace();
							}
					});
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye!");
		try {
			tcp.stop();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}

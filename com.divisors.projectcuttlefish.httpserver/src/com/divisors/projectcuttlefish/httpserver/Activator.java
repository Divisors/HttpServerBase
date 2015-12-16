package com.divisors.projectcuttlefish.httpserver;

import java.net.InetSocketAddress;
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
			tcp = new TcpServerImpl(new InetSocketAddress(8085))
					.start(server->((TcpServerImpl)server).dispatchOn(RingBufferProcessor.create("test1", 32)).runOn(Executors.newCachedThreadPool()));
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

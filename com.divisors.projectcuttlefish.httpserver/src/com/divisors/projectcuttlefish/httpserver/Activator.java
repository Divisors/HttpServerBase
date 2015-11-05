package com.divisors.projectcuttlefish.httpserver;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;
import com.divisors.projectcuttlefish.httpserver.api.Server;
import com.divisors.projectcuttlefish.httpserver.api.ServerFactory;
import com.divisors.projectcuttlefish.httpserver.impl.HttpServerFactoryImpl;
import com.divisors.projectcuttlefish.httpserver.impl.ServerImpl;
import com.sun.security.sasl.ServerFactoryImpl;

import reactor.Environment;

public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServerFactoryServiceRegistration;
	ServiceRegistration<?> serverFactoryServiceRegistration;
	Server server;

	@Override
	public void start(BundleContext context) throws Exception {
		try {
			System.out.println("Initializing: ProjectCuttlefish|HttpServer");
			Environment.initialize();
			serverFactoryServiceRegistration = context.registerService(ServerFactory.class.getName(),new ServerFactoryImpl(), null);
			httpServerFactoryServiceRegistration = context.registerService(HttpServerFactory.class.getName(), new HttpServerFactoryImpl(), null);
			server = new ServerImpl(new InetSocketAddress("localhost", 8082));
			server.init();
			server.start(Executors.newSingleThreadExecutor());
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye!");
		httpServerFactoryServiceRegistration.unregister();
		serverFactoryServiceRegistration.unregister();
		
		try {
			Environment.terminate();
			server.stop();
			server.destroy();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}

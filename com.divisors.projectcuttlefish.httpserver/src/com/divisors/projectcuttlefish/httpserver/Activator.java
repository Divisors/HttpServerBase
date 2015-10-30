package com.divisors.projectcuttlefish.httpserver;

import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;
import com.divisors.projectcuttlefish.httpserver.impl.HttpServerFactoryImpl;

public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServerFactoryServiceRegistration;
	HttpServer server;

	@Override
	public void start(BundleContext context) throws Exception {
		try {
		System.out.println("Initializing: ProjectCuttlefish|HttpServer");
		HttpServerFactory httpService = new HttpServerFactoryImpl();
		httpServerFactoryServiceRegistration = context.registerService(HttpServerFactory.class.getName(), httpService, null);
		server = httpService.createServer(8081);
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
		try {
			server.stop();
			server.destroy();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}

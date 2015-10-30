package com.divisors.projectcuttlefish.httpserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;
import com.divisors.projectcuttlefish.httpserver.impl.HttpServerFactoryImpl;

public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServerFactoryServiceRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Initializing: ProjectCuttlefish|HttpServer");
		HttpServerFactory httpService = new HttpServerFactoryImpl();
		httpServerFactoryServiceRegistration = context.registerService(HttpServerFactory.class.getName(), httpService, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		httpServerFactoryServiceRegistration.unregister();
	}

}

package com.divisors.projectcuttlefish.httpserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServiceRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("Initializing: ProjectCuttlefish|HttpServer");
		HttpServerService httpService = new HttpServerServiceImpl();
		httpServiceRegistration = context.registerService(HttpServerService.class.getName(), httpService, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		httpServiceRegistration.unregister();
	}

}

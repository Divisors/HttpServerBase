package com.divisors.projectcuttlefish.httpserver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.HttpServerFactory;
import com.divisors.projectcuttlefish.httpserver.impl.HttpServerFactoryImpl;

public class Activator implements BundleActivator {
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	protected ServiceRegistration<?> httpServerFactoryService;

	/**
	 * 
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		System.out.println("Initializing: ProjectCuttlefish|HttpServer");
		HttpServerFactory httpService = new HttpServerFactoryImpl();
		httpServerFactoryService = context.registerService(HttpServerFactory.class.getName(), httpService, null);
	}
	
	/**
	 * 
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.context = null;
		httpServerFactoryService.unregister();
	}

}

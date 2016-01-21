package com.divisors.projectcuttlefish.contentmanager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.contentmanager.api.ViewManager;
import com.divisors.projectcuttlefish.contentmanager.api.updater.GitAutoUpdateService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	protected ServiceRegistration<?> viewManagerService;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		System.out.println("Initializing: ProjectCuttlefish|View Manager");
		ViewManager viewManager = ViewManager.getInstance();
		viewManagerService = context.registerService(ViewManager.class.getName(), viewManager, null);
		try {
			new GitAutoUpdateService().watch("Divisors", "Project-Cuttlefish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		viewManagerService.unregister();
	}

}

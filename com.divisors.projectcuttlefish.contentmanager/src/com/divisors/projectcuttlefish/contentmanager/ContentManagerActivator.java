package com.divisors.projectcuttlefish.contentmanager;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.contentmanager.api.ResourceCache;
import com.divisors.projectcuttlefish.contentmanager.api.ResourceCacheImpl;
import com.divisors.projectcuttlefish.contentmanager.api.ResourceHttpServlet;
import com.divisors.projectcuttlefish.contentmanager.api.ViewManager;
import com.divisors.projectcuttlefish.contentmanager.api.gh.GitRepositorySynchronizer;
import com.divisors.projectcuttlefish.contentmanager.api.resource.FileResource;
import com.divisors.projectcuttlefish.httpserver.HttpServerActivator;
import com.divisors.projectcuttlefish.httpserver.api.Version;

public class ContentManagerActivator implements BundleActivator {

	private static ContentManagerActivator instance;
	protected BundleContext context;
	protected Path stateLoc;

	public static ContentManagerActivator getInstance() {
		return instance;
	}
	public BundleContext getContext() {
		return this.context;
	}
	public Path getStateLocation() {
		return this.stateLoc;
	}
	
	protected ServiceRegistration<?> viewManagerService;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
		System.out.println("Initializing: ProjectCuttlefish|View Manager");
		ViewManager viewManager = ViewManager.getInstance();
		viewManagerService = context.registerService(ViewManager.class.getName(), viewManager, null);
		{
			IPath path = Platform.getStateLocation(context.getBundle());
			if (path != null)
				stateLoc = path.makeAbsolute().toFile().toPath();
		}
		System.out.println("Home dir: " + stateLoc);
		ResourceCache cache = new ResourceCacheImpl();
		ResourceHttpServlet servlet = new ResourceHttpServlet(HttpServerActivator.getInstance().http, cache);
		cache.put(new FileResource(new File("File location here"), new Version(0,0,0)));
		{
			File localRepo = new File(stateLoc.toFile(), "foo");
			localRepo.mkdir();
			GitRepositorySynchronizer git = new GitRepositorySynchronizer(localRepo, "git@github.com:Divisors/Project-Cuttlefish.git", "master");
			git.update();
		}
		try {
//			new GitAutoUpdateService().watch("Divisors", "Project-Cuttlefish");
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
		ContentManagerActivator.instance = null;
		viewManagerService.unregister();
	}
}

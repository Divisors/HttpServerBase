package com.divisors.projectcuttlefish.contentmanager;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.contentmanager.api.ResourceCache;
import com.divisors.projectcuttlefish.contentmanager.api.ResourceCacheImpl;
import com.divisors.projectcuttlefish.contentmanager.api.ResourceHttpServlet;
import com.divisors.projectcuttlefish.contentmanager.api.ViewManager;
import com.divisors.projectcuttlefish.contentmanager.api.resource.DirectoryResourceLoader;
import com.divisors.projectcuttlefish.contentmanager.api.resource.StaticFileResource;
import com.divisors.projectcuttlefish.httpserver.HttpServerActivator;
import com.divisors.projectcuttlefish.httpserver.api.Version;
import com.divisors.projectcuttlefish.httpserver.client.HttpClient;

import reactor.bus.EventBus;
import reactor.core.processor.RingBufferProcessor;

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
		@SuppressWarnings("unused")
		ResourceHttpServlet servlet = new ResourceHttpServlet(HttpServerActivator.getInstance().http, cache);
//		{
//			Processor processor = RingBufferProcessor.create("pc.server.1", 32);
//			HttpClient client = new HttpClient(EventBus.create(processor), Executors.newCachedThreadPool());
//			client.init();
//			client.start();
//			GithubApiService github = new GithubApiService(client);
//			GithubUser user = github.getUserByName("Divisors");
//			github.query(user);
//		}
		for (StaticFileResource file : (new DirectoryResourceLoader(Paths.get("Source Directory"), (x)->(new Version(0,0,0)))).load())
			cache.put(file);
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

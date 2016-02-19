
package com.divisors.projectcuttlefish.crypto;

import java.nio.file.Path;
import java.util.Arrays;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.divisors.projectcuttlefish.crypto.impl.AcmePrivateKey;

public class Activator implements BundleActivator {
	private static Activator instance;
	protected BundleContext context;
	protected Path stateLoc;

	public static Activator getInstance() {
		return instance;
	}

	public BundleContext getContext() {
		return context;
	}
	public Path getStateLocation() {
		return stateLoc;
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		this.context = context;
		IPath path = Platform.getStateLocation(context.getBundle());
		if (path != null)
			stateLoc = path.makeAbsolute().toFile().toPath();
		AcmePrivateKey key = new AcmePrivateKey(Arrays.asList("example.com"));
		key.update();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		Activator.instance = null;
	}
}

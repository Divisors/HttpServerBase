package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import com.divisors.projectcuttlefish.contentmanager.ContentManagerActivator;

public class PluginManagerCLI implements CommandProvider {
	
	@Override
	public String getHelp() {
		return "jsp <cmd>";
	}
	
	public void plugin(CommandInterpreter ci) throws Exception {
		BundleContext context = ContentManagerActivator.getInstance().getContext();
		String vendor = context.getProperty(Constants.FRAMEWORK_VENDOR);
		String version = context.getProperty(Constants.FRAMEWORK_VERSION);
		String osName = context.getProperty(Constants.FRAMEWORK_OS_NAME);
		String osVersion = context.getProperty(Constants.FRAMEWORK_OS_VERSION);
		System.out.println("\n " + vendor + " " 
				+ version + " (" + osName + " " 
				+ osVersion + ")");
	}

}

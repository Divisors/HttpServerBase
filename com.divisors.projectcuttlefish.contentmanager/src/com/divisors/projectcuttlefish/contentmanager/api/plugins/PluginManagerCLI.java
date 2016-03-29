package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

import com.divisors.projectcuttlefish.contentmanager.ContentManagerActivator;

public class PluginManagerCLI implements CommandProvider {
	protected Path cd = (new File(System.getProperty("user.dir"))).toPath();
	@Override
	public String getHelp() {
		return "jsp <cmd>";
	}
	
	public void _jsp(CommandInterpreter ci) throws Exception {
		List<String> arguments = new LinkedList<>();
		String arg;
		for (int i = 0; i < 10 && (arg = ci.nextArgument()) != null; i++)
			arguments.add(arg);
		if (!arguments.isEmpty()) {
			switch (arguments.get(0)) {
				case "pwd":
					System.out.println(cd);
					break;
				case "cd": {
					if (arguments.size() < 2) {
						System.err.println("\tUsage: jsp cd <path>");
						break;
					}
					String rel = arguments.get(1);
					if (rel.startsWith("~")) {
						cd = Paths.get(System.getProperty("user.home"), rel.substring(1));
					} else if (rel.startsWith("/")) {
						File[] roots = FileSystemView.getFileSystemView().getRoots();
						for (File root : roots)
							System.out.println(root);
						cd = roots[0].toPath().resolve(rel.substring(1));
					} else {
						cd = cd.resolve(rel);
					}
					System.out.println("\t" + (cd = cd.normalize()));
					break;
				}
				case "ls": {
					for (File f : cd.toFile().listFiles())
						System.out.println("\t" + cd.relativize(f.toPath()) + (f.isDirectory()?"/":""));
					break;
				}
				case "load": {
					if (arguments.size() < 2) {
						System.err.println("\tUsage: jsp load <path>");
						break;
					}
					Path path = cd;
					path = path.resolve(arguments.get(1)).normalize();
					Path pluginJs = path.resolve("plugin.js").normalize();
					System.out.println("\tSearching " + path);
					if (!Files.exists(pluginJs)) {
						System.err.println("\tUnable to locate plugin.js at " + pluginJs);
						break;
					}
					break;
				}
			}
		}
		BundleContext context = ContentManagerActivator.getInstance().getContext();
//		String vendor = context.getProperty(Constants.FRAMEWORK_VENDOR);
//		String version = context.getProperty(Constants.FRAMEWORK_VERSION);
//		String osName = context.getProperty(Constants.FRAMEWORK_OS_NAME);
//		String osVersion = context.getProperty(Constants.FRAMEWORK_OS_VERSION);
//		System.out.println("\n " + vendor + " " 
//				+ version + " (" + osName + " " 
//				+ osVersion + ")");
	}

}

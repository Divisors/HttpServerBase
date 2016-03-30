package com.divisors.projectcuttlefish.contentmanager.api.plugins;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;

import com.divisors.projectcuttlefish.contentmanager.ContentManagerActivator;

/**
 * 
 * @author mailmindlin
 */
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
					Path pluginManifest = path.resolve("plugin.json").normalize();
					System.out.println("\tSearching " + pluginManifest);
					if (!Files.exists(pluginManifest)) {
						System.err.format("\tUnable to locate plugin manifest at %s%n",pluginManifest);
						break;
					}
					try {
						PluginManager.INSTANCE.loadPlugin(pluginManifest);
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
				case "status":
				case "list": {
					Map<Long, Plugin> plugins = PluginManager.INSTANCE.getPlugins();
					if (plugins.isEmpty()) {
						System.err.println("0 plugins active");
						break;
					}
					plugins.values().parallelStream()
						.sorted((a,b)->(a.getId() > b.getId() ? 1 : a.getId() < b.getId() ? -1 : 0))
						.map((plugin) -> (" " + plugin.getId() + "\t" + plugin.getName()))
						.forEachOrdered(System.out::println);
					break;
				}
				case "activate": {
					if (arguments.size() < 2) {
						System.err.println("\tUsage: jsp load <plugin ID>");
						break;
					}
					Long id;
					try {
						id = Long.parseLong(arguments.get(1));
					} catch (NumberFormatException e) {
						System.err.format("Invalid plugin ID '%s'%n", arguments.get(1));
						break;
					}
					Plugin plugin = PluginManager.INSTANCE.getPluginByID(id);
					if (plugin == null) {
						System.err.format("There is no plugin with the id #%d%n", id);
						break;
					}
					System.out.format("Activating plugin #%d (%s)%n", id, plugin.getName());
					
					break;
				}
				default: {
					System.err.println("Usage: jsp <pwd | cd | ls | load | list>");
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

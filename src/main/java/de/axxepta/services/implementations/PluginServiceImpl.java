package de.axxepta.services.implementations;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.models.PluginDescriptionModel;
import de.axxepta.services.interfaces.IPluginService;
import ro.sync.ecss.extensions.api.webapp.WebappAuthorDocumentFactory;
import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;
import ro.sync.exml.plugin.PluginManager;
import ro.sync.exml.plugin.PluginUtil;


@Service(name = "PluginService")
@Singleton
public class PluginServiceImpl implements IPluginService {

	private static final Logger LOG = Logger.getLogger(PluginServiceImpl.class);

	@Override
	public String getPluginDefaultDirectory() {
		PluginManager pluginManager = PluginManager.getInstance();
		String pluginDefaultDirectory = pluginManager.getDefaultPluginsDir();
		LOG.info("Plugin default directory is " + pluginDefaultDirectory);
		return pluginDefaultDirectory;
	}
	
	@Override
	public List<File> getPluginDirectories() {
		List<File> pluginDirsList = PluginUtil.getPluginDirs();
		LOG.info("Number of founded plugin directories is " + pluginDirsList.size());
		return pluginDirsList;
	}
	
	@Override
	public boolean changeDirectoryPlugin(String path) {
		File pathPlugins = new File(path);
		if(!pathPlugins.exists() || !pathPlugins.isDirectory()) {
			LOG.error(path + " not exist or isn't a directory");
			return false;
		}
		LOG.info("Set path " + path + " as an plugin directory");
		
		System.setProperty("com.oxygenxml.editor.plugins.dir", path);
		
		WebappAuthorDocumentFactory.dispose();
		WebappAuthorDocumentFactory.init();
		return true;
	}

	@Override
	public List<PluginDescriptionModel> getDescriptionPlugins() {
		List<Plugin> pluginsList = getPlugins();
		List <PluginDescriptionModel>  pluginsDescriptionList = pluginsList.stream().map(plugin -> {
			PluginDescriptor pluginDescriptor = plugin.getDescriptor();
			return new PluginDescriptionModel(pluginDescriptor.getID(), pluginDescriptor.getName(),
					pluginDescriptor.getDescription(), pluginDescriptor.getVendor(), pluginDescriptor.getVersion(),
					pluginDescriptor.getConfigUrlPath());
		}).collect(Collectors.toList());
		LOG.info("Return a number of " + pluginsList.size() + " descriptions of plugins");
		return pluginsDescriptionList;
	}

	private List<Plugin> getPlugins() {
		PluginManager pluginManager = PluginManager.getInstance();
		LOG.info("Get all plugins");
		return pluginManager.getPlugins();
	}
}

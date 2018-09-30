package de.axxepta.services.interfaces;

import java.io.File;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import de.axxepta.models.PluginDescriptionModel;

@Contract
public interface IPluginService {

	public String getPluginDefaultDirectory();
	
	public List<File> getPluginDirectories();
	
	public boolean changeDirectoryPlugin(String path);
	
	public List<PluginDescriptionModel> getDescriptionPlugins();
}

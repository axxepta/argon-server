package de.axxepta.models;

public class PluginDescriptionModel {

	private String id;
	private String name;
	private String description;
	private String vendor;
	private String version;
	private String configURLPath;

	public PluginDescriptionModel() {
		super();
	}

	public PluginDescriptionModel(String id, String name, String description, String vendor, String version,
			String configURLPath) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;	
		this.vendor = vendor;
		this.version = version;
		this.configURLPath = configURLPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getConfigURLPath() {
		return configURLPath;
	}

	public void setConfigURLPath(String configURLPath) {
		this.configURLPath = configURLPath;
	}

	@Override
	public String toString() {
		return "{id=" + id + ", name=" + name + ", description=" + description + ", vendor="
				+ vendor + ", version=" + version + ", configURLPath=" + configURLPath + "}";
	}	
}

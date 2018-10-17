package de.axxepta.configurator;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleReader {
		
		private final ResourceBundle resourceBundle;
		
		public ResourceBundleReader(File fileResource, Locale localeSufix) {
			resourceBundle = ResourceBundle.getBundle(fileResource.getPath(), localeSufix);
		}
		
		public List <String> getKeys() {
			return Collections.list(resourceBundle.getKeys());
		}
		
		public boolean valueTypeIsArray(String key, char separator) {
			String value = resourceBundle.getString(key);
			if(value.contains("" + separator))
				return true;
			return false;
		}
		
		public String getValueAsString(String key) {
			return resourceBundle.getString(key);		
		}
		
		public String [] getValueAsArray(String key, char separator) {
			return resourceBundle.getString(key).split("" + separator);
		}
	}


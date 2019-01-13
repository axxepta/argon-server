package de.axxepta.tools.interfaces;

import java.io.File;

@FunctionalInterface
public interface ITransformJSONToXML {
	
	public String transformToXML(File doc);
}

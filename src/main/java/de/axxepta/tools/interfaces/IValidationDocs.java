package de.axxepta.tools.interfaces;

import java.io.File;

@FunctionalInterface
public interface IValidationDocs {
	
	public Boolean isDocTypeValid(File doc);
}

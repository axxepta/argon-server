package de.axxepta.services.interfaces;

import java.io.File;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IDatabaseResourceService {
	
	public boolean uploadFileToDatabase(File file, String database);
	
	public boolean deleteFileFromDatabase(File file, String database);
	
	public boolean testDB(String resourceName);
	
	public String showInfosDatabase(String databaseName);
}

package de.axxepta.services.interfaces;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IDatabaseResourceService {

	public boolean uploadFile(URL fileURL, String fileRef);
	
	public String calculateHashSum(File filePathName);
	
	public boolean existFileStored(String fileName);
	
	public char [] readingFile(String fileName);
	
	public List<String> listFiles();
	
	public boolean deleteFile(String fileName);
	
	public boolean testDB(String resourceName);
	
	public String showDatabases();
	
	public String showInfosDatabase(String databaseName);
}
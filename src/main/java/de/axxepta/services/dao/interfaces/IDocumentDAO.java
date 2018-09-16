package de.axxepta.services.dao.interfaces;

import java.io.File;
import java.util.Map;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IDocumentDAO {
	
	public void executeQuery(String resourceDatabase, String query);
	
	public Map<String, Map<String, String>> showDatabases();
	
	public String showInfoDatabase(String databaseName);
	
	public Boolean createDatabase(String databaseName, String fileURL);
	
	public Boolean dropDatabase(String databaseName);
	
	public boolean test(String resourceName);
	
	public byte [] readDocument(String documentName, String databaseName);
	
	public boolean uploadDocument(File file, String databaseName);
	
	public boolean deleteDocument(String fileName, String databaseName);

}

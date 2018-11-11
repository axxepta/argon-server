package de.axxepta.services.dao.interfaces;

import java.io.File;
import java.util.Map;

import org.jvnet.hk2.annotations.Contract;
import org.w3c.dom.Document;

@Contract
public interface IDocumentDAO {
	
	public void executeQuery(String resourceDatabase, String query);
	
	public Map<String, Map<String, String>> showDatabases();
	
	public String showInfoDatabase(String databaseName);
	
	public Boolean createDatabase(String databaseName, String fileURL);
	
	public Boolean dropDatabase(String databaseName);
	
	public boolean test(String resourceName);
	
	public Document readXMLDocument(String documentName, String databaseName);
	
	public int uploadXMLDocument(File documentName, boolean withSchemaValidation, String databaseName);
	
	public int deleteDocument(String fileName, String databaseName);

}

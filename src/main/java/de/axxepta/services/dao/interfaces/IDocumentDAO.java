package de.axxepta.services.dao.interfaces;

import java.io.File;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IDocumentDAO {

	public static enum Method {GET, POST, PUT, DELETE};
	
	public void executeQuery(String resourceDatabase, String query);
	
	public String showDatabases();
	
	public String showInfoDatabase(String databaseName);
	
	public boolean createDatabase(File file);
	
	public boolean dropDatabase(String databaseName);
	
	public boolean test(String resourceName);
	
	public void setMethod(Method method);
	
	public byte [] readDocument(String documentName);
	
	public boolean uploadDocument(String documentName, byte [] content);
	
	public boolean renameDocument(String oldDocumentName, String newDocumentName);
	
	public boolean updateDocumentContent(String documentName, byte [] newContent);
	
	public boolean deleteDocument(String fileName);
}

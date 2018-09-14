package de.axxepta.services.implementations;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.dao.interfaces.IDocumentDAO;
import de.axxepta.services.interfaces.IDatabaseResourceService;

@Service(name = "DatabaseBaseXServiceImplementation")
@Singleton
public class DatabaseResourceServiceImpl implements IDatabaseResourceService {

	private static final Logger LOG = Logger.getLogger(DatabaseResourceServiceImpl.class);

	@Inject
	@Named("BaseXDao")
	private IDocumentDAO documentDAO;

	@Override
	public boolean uploadFileToDatabase(File file, String database) {
		LOG.info("Upload file with name " + file.getName() + " in database " + database);
		return false;
	}

	@Override
	public boolean deleteFileFromDatabase(File file, String database) {
		LOG.info("Delete file with name " + file.getName() + " from database " + database);
		return false;
	}

	@Override
	public boolean testDB(String resourceName) {
		LOG.info("test resource " + resourceName);
		return documentDAO.test(resourceName);
	}

	@Override
	public String showInfosDatabase(String databaseName) {
		return documentDAO.showInfoDatabase(databaseName);
	}
}

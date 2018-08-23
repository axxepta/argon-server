package de.axxepta.dao.implementations;

import java.io.File;

import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.List;
import org.basex.core.cmd.Open;

public class RunDirectCommands {

	private static final Logger LOG = Logger.getLogger(RunDirectCommands.class);

	private Context ctx;

	public RunDirectCommands() {
		ctx = new Context();
	}

	public void createDatabase(File file) throws BaseXException {	
		new CreateDB(file.getName(), file.getPath()).execute(ctx);
		LOG.info("Create database " + file.getName());
	}

	public String showInfoDatabase(String nameDatabase) throws BaseXException {
		
		new Open(nameDatabase).execute(ctx);
		String infos = new InfoDB().execute(ctx);
		LOG.info("show infos about "+ nameDatabase + " database");
		return infos;
	}

	public String showExistingDatabase() throws BaseXException {
		LOG.info("show infos about available databases");
		return new List().execute(ctx);
	}

	public void dropDatabase(String databaseName) throws BaseXException {		
		new DropDB(databaseName).execute(ctx);
		LOG.info("drop database with name " + databaseName);
	}
	
	public void closeContext() {
		if (ctx != null) {
			ctx.close();
			LOG.info("close context for BaseX direct commands");
		}
	}
}

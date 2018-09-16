package de.axxepta.dao.implementations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.Databases;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.InfoDB;
import org.basex.core.cmd.Open;
import org.basex.core.users.User;
import org.basex.core.users.Users;
import org.basex.server.Sessions;
import org.basex.util.list.StringList;

public class RunDirectCommands {

	private static final Logger LOG = Logger.getLogger(RunDirectCommands.class);

	private Context ctx;

	public RunDirectCommands() {
		ctx = new Context();
	}

	public String getInfoSessions() {
		Sessions sessions = ctx.sessions;
		String infoSessions = sessions.info();
		return infoSessions;
	}

	public void createDatabase(String databaseName, File file) throws BaseXException {
		if (file == null) {
			new CreateDB(databaseName).execute(ctx);
			LOG.info("Create empty database with name " + databaseName);
		} else {
			new CreateDB(databaseName, file.getAbsolutePath()).execute(ctx);
			LOG.info("Create database that contain file from " + file.getAbsolutePath());
		}
	}

	public String showInfoDatabase(String nameDatabase) throws BaseXException {
		new Open(nameDatabase).execute(ctx);
		String infos = new InfoDB().execute(ctx);
		LOG.info("show infos about " + nameDatabase + " database");
		return infos;
	}

	public String showExistingDatabase() throws BaseXException {
		LOG.info("show infos about available databases");
		return new org.basex.core.cmd.List().execute(ctx);
	}

	public void dropDatabase(String databaseName) throws BaseXException {
		new DropDB(databaseName).execute(ctx);
		LOG.info("drop database with name " + databaseName);
	}

	public void addUser(String username, String password) {
		User user = new User(username, password);
		Users users = ctx.users;
		users.add(user);
	}

	public boolean dropUser(String username, String password) {
		User user = new User(username, password);
		Users users = ctx.users;
		return users.drop(user);
	}

	public boolean dropUser(String username) {
		Users users = ctx.users;
		User user = users.get(username);
		if (user == null) {
			LOG.error("User with username " + username + " not exist");
			return false;
		}
		return users.drop(user);
	}

	public List<String> listUsername(String database) {
		Users users = ctx.users;
		ArrayList<User> userList = users.users(database, ctx);
		List<String> usernameList = new ArrayList<>();
		for (User user : userList) {
			String username = user.name();
			usernameList.add(username);
		}
		return usernameList;
	}

	public String[] listDatabases() {
		Databases databases = ctx.databases;
		StringList list = databases.list();
		String[] namesDatabaseArray = list.toArray();
		LOG.info("name databases " + Arrays.toString(namesDatabaseArray));
		return namesDatabaseArray;
	}

	public boolean existDatabase(String databaseName) {
		List<String> databaseNameList = Arrays.asList(listDatabases());
		if (databaseNameList.contains(databaseName))
			return true;
		return false;
	}

	public Map<String, String> getDatabaseInfo(String nameDatabase) throws BaseXException {
		String info = showInfoDatabase(nameDatabase);
		String lines[] = info.split("\\r?\\n");
		List<String> infoList = Arrays.asList(lines);
		Map<String, String> infoMap = new HashMap<>();
		for (String infoElem : infoList) {
			if (!infoElem.contains(":"))
				continue;
			String[] s = infoElem.split(":");
			infoMap.put(s[0].trim().toLowerCase(), s[1].trim());
		}
		return infoMap;
	}

	public void closeContext() {
		if (ctx != null) {
			ctx.close();
			LOG.info("close context for BaseX direct commands");
		}
	}
}

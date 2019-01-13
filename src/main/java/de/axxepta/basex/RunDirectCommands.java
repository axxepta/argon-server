package de.axxepta.basex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
import org.basex.data.Data;
import org.basex.io.out.DataOutput;
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

	public void uploadContextFile(byte [] byteArray, String databaseName) throws IOException {
		new Open(databaseName).execute(ctx);
		Data data = ctx.data();
		OutputStream bOutStream = new ByteArrayOutputStream();
		DataOutput out = new DataOutput(bOutStream);
		out.write(byteArray);
		
		data.elemNames.write(out);
		LOG.info("uploaded a file");
	}
	
	public int numberElements(String databaseName) throws BaseXException{
		new Open(databaseName).execute(ctx);
		Data data = ctx.data();
		int numberElements = data.elemNames.size();
		LOG.info("number of elements for database " + databaseName + " is " + numberElements);
		
		return numberElements;
	}
	
	
	public List<String> listElements(String databaseName) throws BaseXException{
		LOG.info("List document for database " + databaseName);
		new Open(databaseName).execute(ctx);
		Data data = ctx.data();
		int numberElements = data.elemNames.size();
		
		List <String> elements = new ArrayList<> ();
		for(int i = 1; i <= numberElements; i++)
			elements.add(new String(data.elemNames.key(i), StandardCharsets.UTF_8));
		
		return elements;
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
	
	public void openDatabase(String databaseName) throws BaseXException {
		new Open(databaseName).execute(ctx);
	}

	public String showInfoDatabase(String nameDatabase) throws BaseXException {
		new Open(nameDatabase).execute(ctx);
		String infos = new InfoDB().execute(ctx);
		LOG.info("show infos about " + nameDatabase + " database");
		return infos;
	}

	public String showExistingDatabase() throws BaseXException {
		LOG.info("show infos about available databases");
		String list = new org.basex.core.cmd.List().execute(ctx);
		return list;
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
		LOG.info("Drop user with name " + username);
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
	
	public void close() {
		ctx.close();
	}
}

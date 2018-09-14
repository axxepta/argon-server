package de.axxepta.dao.implementations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.basex.core.BaseXException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.services.dao.interfaces.IDocumentDAO;

@Service(name = "BaseXDao")
@Singleton
public class DocumentDAOImpl implements IDocumentDAO {

	private static final Logger LOG = Logger.getLogger(DocumentDAOImpl.class);

	@Context
	private HttpServletRequest request;

	private RunDirectCommands runCommands;

	private Client client;

	private String scheme;
	private int port;
	private String baseURL;
	private String username;
	private String password;

	@PostConstruct
	public void initConnection() {
		scheme = request.getScheme();
		port = request.getLocalPort();
		baseURL = "argon-server/argon-rest/rest";

		username = "admin";
		password = "admin";

		runCommands = new RunDirectCommands();
	}

	@Override
	public void executeQuery(String resourceDatabase, String query) {
		LOG.info("Set actual connection for " + resourceDatabase);
	}

	@Override
	public Map<String, Map<String, String>> showDatabases() {

		Map<String, Map<String, String>> infoDatabasesMap = new HashMap<>();
		String[] arrayDatabases = runCommands.listDatabases();

		for (String nameDatabase : arrayDatabases) {
			 Map<String, String> info;
			try {
				info = runCommands.getDatabaseInfo(nameDatabase);
			} catch (BaseXException e) {
				LOG.error("Exception for database with name " + nameDatabase + " : " + e.getMessage());
				continue;
			}
			 infoDatabasesMap.put(nameDatabase, info);
		}

		return infoDatabasesMap;
	}

	@Override
	public String showInfoDatabase(String databaseName) {
		try {
			return runCommands.showInfoDatabase(databaseName);
		} catch (BaseXException e) {
			LOG.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean test(String resource) {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basicBuilder().nonPreemptive()
				.credentials(username, password).build();

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(feature);
		client = ClientBuilder.newClient(clientConfig);
		String urlBaseX = composeURL(resource);
		WebTarget webTarget = client.target(urlBaseX);

		if (webTarget == null) {
			LOG.error("HTTP URL connection is null");
			return false;
		}
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
		Response response = invocationBuilder.get();
		int code;
		try {
			code = response.getStatus();
		} catch (ProcessingException e) {
			LOG.error(e.getMessage());
			client.close();
			return false;
		}
		LOG.info("Response is " + code);
		client.close();
		if (code == 200)
			return true;
		else
			return false;

	}

	@Override
	public byte[] readDocument(String documentName, String database) {
		return null;
	}

	@Override
	public boolean uploadDocument(File file, String database) {
		return false;
	}

	@Override
	public boolean deleteDocument(String fileName, String databaseName) {
		return false;

	}

	@Override
	public boolean createDatabase(File file) {
		try {
			runCommands.createDatabase(file);
		} catch (BaseXException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean dropDatabase(String databaseName) {
		try {
			runCommands.dropDatabase(databaseName);
		} catch (BaseXException e) {
			LOG.error(e.getMessage());
			return false;
		}
		return true;
	}

	private String composeURL(String resourceDatabase) {
		return scheme + "://localhost" + ':' + port + '/' + baseURL + '/' + resourceDatabase;
	}

	@PreDestroy
	public void closeConnections() {
		runCommands.closeContext();
	}

}

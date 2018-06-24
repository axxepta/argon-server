package de.axxepta.database;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectionDBPool {

	private final static String URL_DB = "...";
	
	private final static int MAX_CONNECTIONS = 10;
	private static ConnectionDBPool instance = null;
	private static HttpURLConnection[] connections = new HttpURLConnection[MAX_CONNECTIONS];
	
	private static int counter;

	private ConnectionDBPool() {
	}

	public static ConnectionDBPool getInstance() throws IOException {
		if (instance == null) {
			synchronized (ConnectionDBPool.class) {
				if (instance == null) {
					instance = new ConnectionDBPool();
					initializeConnections();
					counter = 0;
				}
			}
		}

		return instance;
	}

	private static void initializeConnections() throws IOException {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			URL url = new URL(URL_DB);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			connections[i] = conn;
		}
	}

	public static HttpURLConnection getConnection() {
		counter++;
		if (counter == Integer.MAX_VALUE)
			counter = 0;

		return connections[counter % MAX_CONNECTIONS];
	}

}

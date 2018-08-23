package de.axxepta.database;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionDBPoolRest {
	
	private final static int MAX_CONNECTIONS = 10;
	private static ConnectionDBPoolRest instance = null;
	private static HttpURLConnection[] connections = new HttpURLConnection[MAX_CONNECTIONS];
	
	private static int counter;

	private ConnectionDBPoolRest() {
		
	}

	public static synchronized ConnectionDBPoolRest getInstance(final String urlDB) throws IOException {
		if (instance == null) {
			synchronized (ConnectionDBPoolRest.class) {
				if (instance == null) {
					instance = new ConnectionDBPoolRest();
					initializeConnections(urlDB);
					counter = 0;
				}
			}
		}
		return instance;
	}

	private static synchronized void initializeConnections(String urlDB) throws IOException {
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			URL url = new URL(urlDB);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			connections[i] = conn;//assign connection
		}
	}

	public static HttpURLConnection getConnection() {
		counter ++;
		if (counter == Integer.MAX_VALUE)
			counter = 0;

		return connections[counter % MAX_CONNECTIONS];
	}

}

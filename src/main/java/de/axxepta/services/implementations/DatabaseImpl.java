package de.axxepta.services.implementations;

import java.io.IOException;

import javax.inject.Singleton;

import org.jvnet.hk2.annotations.Service;

import de.axxepta.database.ConnectionDBPoolRest;
import de.axxepta.services.interfaces.IDatabase;

@Service(name="DBImpl")
@Singleton
public class DatabaseImpl implements IDatabase{

	private final static String URL_CONN = "...";
	
	@Override
	public boolean testValidityConn() {
		ConnectionDBPoolRest conn = null;
		try {
			conn = ConnectionDBPoolRest.getInstance(URL_CONN);
		} catch (IOException e) {
			return false;
		}
		return true;
	}

}

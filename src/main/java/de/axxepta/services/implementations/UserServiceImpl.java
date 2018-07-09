package de.axxepta.services.implementations;

import java.io.IOException;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;

import de.axxepta.database.ConnectionDBPoolRest;
import de.axxepta.services.interfaces.UserServiceI;

@Service(name="FreshImplementation")
@Singleton
public class UserServiceImpl implements UserServiceI{

	private static final Logger LOG = Logger.getLogger(UserServiceImpl.class);
	
	ConnectionDBPoolRest connDBPool;
	
	public UserServiceImpl() throws IOException {
		//connDBPool = ConnectionDBPool.getInstance();
		
	}
	
	@Override
	public boolean login(String username, String password) {
		LOG.info("Login user with username " + username);
		
		return false;
	}

	@Override
	public boolean register(String username, String password) {
		LOG.info("Register user with username " + username);
		return false;
	}

}

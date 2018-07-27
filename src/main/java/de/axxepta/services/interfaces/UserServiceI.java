package de.axxepta.services.interfaces;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface UserServiceI {

	public boolean login (String username, String password);
	
	public boolean register (String username, String password);
	
	public String getActualUser();
	
	public boolean logout();
}

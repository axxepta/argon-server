package de.axxepta.services.interfaces;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface IAuthUserService {

	public boolean login (String username, String password);
	
	public boolean register (String username, String password);
	
	public String getActualUsername();
	
	public Boolean hasRoleActualUser(String role);
	
	public String resetPasswordActualLoginUser();
	
	public boolean logout();
}

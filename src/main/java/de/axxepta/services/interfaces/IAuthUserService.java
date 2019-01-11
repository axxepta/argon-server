package de.axxepta.services.interfaces;

import java.util.List;

import org.jvnet.hk2.annotations.Contract;

import ro.sync.ecss.extensions.api.webapp.license.UserInfo;

@Contract
public interface IAuthUserService {

	public boolean login (String username, String password);
	
	public boolean register (String username, String password);
	
	public String getActualUsername();
	
	public Boolean hasRoleActualUser(String role);
	
	public String changePasswordActualLoginUser(String password);
	
	public boolean logout();

	public List<UserInfo> getLoggedUsersId();
}

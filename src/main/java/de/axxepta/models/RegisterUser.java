package de.axxepta.models;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.shiro.authz.Permission;

public class RegisterUser {

	private String username;
	private String password;
	private LocalDateTime registerDatetime;
	private LocalDateTime untilDatetime;
	private List<String> roles;
	private List <Permission> listPermissions;
	
	public RegisterUser(String username, String password, LocalDateTime registerDatetime, LocalDateTime untilDatetime,
			List<String> roles, List<Permission> listPermissions) {
		super();
		this.username = username;
		this.password = password;
		this.registerDatetime = registerDatetime;
		this.untilDatetime = untilDatetime;
		this.roles = roles;
		this.listPermissions = listPermissions;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public LocalDateTime getRegisterDatetime() {
		return registerDatetime;
	}

	public void setRegisterDatetime(LocalDateTime registerDatetime) {
		this.registerDatetime = registerDatetime;
	}

	public LocalDateTime getUntilDatetime() {
		return untilDatetime;
	}

	public void setUntilDatetime(LocalDateTime untilDatetime) {
		this.untilDatetime = untilDatetime;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<Permission> getListPermissions() {
		return listPermissions;
	}

	public void setListPermissions(List<Permission> listPermissions) {
		this.listPermissions = listPermissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((listPermissions == null) ? 0 : listPermissions.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((registerDatetime == null) ? 0 : registerDatetime.hashCode());
		result = prime * result + ((roles == null) ? 0 : roles.hashCode());
		result = prime * result + ((untilDatetime == null) ? 0 : untilDatetime.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegisterUser other = (RegisterUser) obj;
		if (listPermissions == null) {
			if (other.listPermissions != null)
				return false;
		} else if (!listPermissions.equals(other.listPermissions))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (registerDatetime == null) {
			if (other.registerDatetime != null)
				return false;
		} else if (!registerDatetime.equals(other.registerDatetime))
			return false;
		if (roles == null) {
			if (other.roles != null)
				return false;
		} else if (!roles.equals(other.roles))
			return false;
		if (untilDatetime == null) {
			if (other.untilDatetime != null)
				return false;
		} else if (!untilDatetime.equals(other.untilDatetime))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}

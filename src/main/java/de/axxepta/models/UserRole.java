package de.axxepta.models;

public class UserRole {

	private Integer id;
	private String roleName;
	
	public UserRole() {
		super();
	}

	public UserRole(Integer id, String roleName) {
		super();
		this.id = id;
		this.roleName = roleName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}

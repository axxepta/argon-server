package de.axxepta.models;

public class RolesPermission {
	private Integer id;
	private String permission;
	private String roleName;
	
	public RolesPermission() {
		super();
	}

	public RolesPermission(Integer id, String permission, String roleName) {
		super();
		this.id = id;
		this.permission = permission;
		this.roleName = roleName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}

package de.axxepta.models;

public class UserDAOModel extends UserAuthModel{

	private int id;
	
	public UserDAOModel(int id, String username, String password) {
		super(username, password);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}

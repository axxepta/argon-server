package de.axxepta.resteasy.login;

import java.time.LocalDateTime;

public class ResponseModel {

	private LocalDateTime dateTime;
	private String user;
	private String password;
	
	public ResponseModel() {
		
	}	
	
	public ResponseModel(String user, String password) {
		super();
		this.dateTime = LocalDateTime.now();
		this.user = user;
		this.password = password;
	}


	public LocalDateTime getDateTime() {
		return dateTime;
	}
	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ResponseModel [dateTime=" + dateTime + ", user=" + user + ", password=" + password + 
				 "]";
	}
	
}

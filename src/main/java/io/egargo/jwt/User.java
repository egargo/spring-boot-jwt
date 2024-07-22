package io.egargo.jwt;

public class User {
	private String username;
	private String email;
	private String password;

	public User() {
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getUsername() {
		return this.username;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}
}

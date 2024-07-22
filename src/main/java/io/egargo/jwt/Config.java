package io.egargo.jwt;

// import org.springframework.context.annotation.Bean;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// @Configuration
public class Config {
	public String USERNAME = "admin";
	public String PASSWORD = "admin12";

	public Config() {
	}

	public String getUsername() {
		return this.USERNAME;
	}

	public String getPassword() {
		return this.PASSWORD;
	}

	// @Bean
	// public BCryptPasswordEncoder passwordEncoder() {
	// return new BCryptPasswordEncoder();
	// }
}

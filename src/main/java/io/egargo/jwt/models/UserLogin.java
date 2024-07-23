package io.egargo.jwt.models;

public class UserLogin {
    public String username;
    public String password;

    public UserLogin() {
    }

    public UserLogin setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserLogin setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}

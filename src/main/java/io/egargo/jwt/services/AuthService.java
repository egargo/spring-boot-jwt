package io.egargo.jwt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.egargo.jwt.models.UserLogin;

@Service
public class AuthService<T> {
    @Autowired
    JwtService<UserLogin> jwtService = new JwtService<UserLogin>();

    @Autowired
    UserService userService = new UserService();

    /**
     * @param password the plaintext password
     * @return the hashed plaintext password
     */
    public String hashPassword(char[] password) {
        return BCrypt.withDefaults().hashToString(12, password);
    }

    /**
     * @param password       the plaintext password
     * @param hashedPassword the hash of the password
     * @return the result when verifying the password and the hashedPassword
     */
    public BCrypt.Result verifyPassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(),
                hashedPassword);
    }
}

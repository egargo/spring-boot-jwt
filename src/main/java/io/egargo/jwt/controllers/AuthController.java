package io.egargo.jwt.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;

import io.egargo.jwt.services.AuthStrategyImpl;
import io.egargo.jwt.services.JwtService;
import io.egargo.jwt.models.User;
import io.egargo.jwt.models.UserLogin;
import io.egargo.jwt.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    UserService userService = new UserService();

    @Autowired
    JwtService<User> jwtService = new JwtService<User>();

    @Autowired
    AuthStrategyImpl authStrategyImpl = new AuthStrategyImpl();

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        String hashedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        userService.createUser(user.setPassword(hashedPassword));
        return new ResponseEntity<>("Ok", HttpStatusCode.valueOf(201));
    }

    /**
     * This route accepts the UserLogin object as data (in JSON format). This checks
     * if the user exists, if it does, the login password will be matched against
     * the hashed password from the database.
     *
     * If the username and password are all valid, this route will send the
     * accessToken and the refreshToken as a response.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLogin userLogin) {
        try {
            Optional<User> user = userService.getUserByUsername(userLogin.username);

            if (!user.isPresent()) {
                return new ResponseEntity<>("Username and/or password not found", HttpStatusCode.valueOf(404));
            }

            BCrypt.Result checkPassword = BCrypt.verifyer().verify(userLogin.password.toCharArray(),
                    user.get().getPassword());

            if (!checkPassword.verified) {
                return new ResponseEntity<>("Incorrect password", HttpStatusCode.valueOf(200));
            }

            ArrayList<HashMap<String, String>> tokens = new ArrayList<>();
            HashMap<String, String> token = new HashMap<>();
            String accessToken = jwtService.generateAccessToken(userLogin.getUsername(), user.get());
            String refreshToken = jwtService.generateRefreshToken(userLogin.getUsername(), user.get());
            token.put("accessToken", accessToken);
            token.put("refreshToken", refreshToken);
            tokens.add(token);

            return new ResponseEntity<>(tokens, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        try {
            return new ResponseEntity<>("Logout", HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedResource(HttpServletRequest request) {
        try {
            return authStrategyImpl.authorizationCheck(request);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred",
                    HttpStatusCode.valueOf(500));
        }
    }
}

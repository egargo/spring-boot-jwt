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

import jakarta.servlet.http.HttpServletRequest;
import io.egargo.jwt.services.AuthService;
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
    JwtService<String> jwtService = new JwtService<String>();

    @Autowired
    AuthStrategyImpl authStrategyImpl = new AuthStrategyImpl();

    @Autowired
    AuthService<User> authService = new AuthService<>();

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            String hashedPassword = authService.hashPassword(user.getPassword().toCharArray());
            userService.createUser(user.setPassword(hashedPassword));

            return new ResponseEntity<>("Successfully created user account", HttpStatusCode.valueOf(201));
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatusCode.valueOf(500));
        }
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

            if (!authService.verifyPassword(userLogin.password, user.get().getPassword()).verified) {
                return new ResponseEntity<>("Incorrect password", HttpStatusCode.valueOf(200));
            }

            ArrayList<HashMap<String, String>> tokens = new ArrayList<>();
            HashMap<String, String> token = new HashMap<>();
            String accessToken = jwtService.generateAccessToken(userLogin.getUsername());
            String refreshToken = jwtService.generateRefreshToken(userLogin.getUsername());
            token.put("accessToken", accessToken);
            token.put("refreshToken", refreshToken);
            tokens.add(token);

            return new ResponseEntity<>(tokens, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatusCode.valueOf(500));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        try {
            String bearerToken = request.getHeader("Authorization");
            String refreshToken = request.getHeader("refreshToken");

            if (bearerToken == null || bearerToken.split(" ").length <= 0) {
                return new ResponseEntity<>("No authorization token found", HttpStatusCode.valueOf(400));
            }

            if (!jwtService.verifyToken(bearerToken.split(" ")[1])) {
                return new ResponseEntity<>("Expired token", HttpStatusCode.valueOf(400));
            }

            if (refreshToken == null) {
                return new ResponseEntity<>("No refresh token found", HttpStatusCode.valueOf(400));
            }

            if (!jwtService.verifyToken(refreshToken)) {
                return new ResponseEntity<>("You are not authorized to access this resource",
                        HttpStatusCode.valueOf(401));
            }

            Object claim = jwtService.extractAllClaims(bearerToken.split(" ")[1]).get("sub");
            String accessToken = jwtService.generateAccessToken(claim.toString());
            return new ResponseEntity<>(accessToken, HttpStatusCode.valueOf(200));
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred",
                    HttpStatusCode.valueOf(500));
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

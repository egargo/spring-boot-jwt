package io.egargo.jwt;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class JwtApplication {
    UserLogin userLogin = new UserLogin();

    @Autowired
    JwtService<User> jwtServiceUser = new JwtService<User>();
    @Autowired
    JwtService<UserLogin> jwtServiceUserLogin = new JwtService<UserLogin>();

    public static void main(String[] args) {
        SpringApplication.run(JwtApplication.class, args);
    }

    @GetMapping()
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>(new ApiResponse<>().setMessage("Hello"), HttpStatusCode.valueOf(200));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLogin user) {
        Config config = new Config();
        userLogin.setUsername(user.username).setPassword(user.password);

        if (userLogin.getUsername().equals(config.getUsername())
                && userLogin.getPassword().equals(config.getPassword())) {
            ArrayList<HashMap<String, String>> tokens = new ArrayList<>();
            HashMap<String, String> token = new HashMap<>();

            jwtServiceUserLogin.generateRefreshToken(userLogin.getUsername(), user);
            token.put("refreshToken", jwtServiceUserLogin.getRefreshToken());
            jwtServiceUserLogin.generateToken(userLogin.getUsername(), user);
            token.put("token", jwtServiceUserLogin.getToken());
            tokens.add(token);

            return new ResponseEntity<>(tokens, HttpStatusCode.valueOf(200));

        } else {
            return new ResponseEntity<>(
                    new ApiResponse<>().setMessage("Invalid username and/or password"),
                    HttpStatusCode.valueOf(404));
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protected_route() {
        if (userLogin.getUsername() != null && userLogin.getPassword() != null)
            if (!userLogin.getUsername().isEmpty() && !userLogin.getPassword().isEmpty()) {
                String token = jwtServiceUserLogin.getToken();
                if (token == null) {
                    return new ResponseEntity<>(
                            new ApiResponse<>().setMessage("You have NO access to this resource"),
                            HttpStatusCode.valueOf(401));
                }
                return new ResponseEntity<>(
                        new ApiResponse<>().setMessage("You have access to this resource"),
                        HttpStatusCode.valueOf(200));
            }
        return new ResponseEntity<>(
                new ApiResponse<>().setMessage("You are not logged in"),
                HttpStatusCode.valueOf(401));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        userLogin.setUsername("").setPassword("");
        jwtServiceUser.setToken("");

        return new ResponseEntity<>(new ApiResponse<>().setMessage("Logged out"), HttpStatusCode.valueOf(200));
    }
}

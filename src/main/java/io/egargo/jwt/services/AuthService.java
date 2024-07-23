// package io.egargo.jwt.services;
//
// import java.util.Optional;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
//
// import io.egargo.jwt.models.User;
// import io.egargo.jwt.models.UserLogin;
//
// @Service
// public class AuthService<T> {
// @Autowired
// JwtService<UserLogin> jwtService = new JwtService<UserLogin>();
//
// @Autowired
// UserService userService = new UserService();
//
// public void checkLoginUser(UserLogin userLogin) {
// Optional<User> user = userService.getUserByUsername(userLogin.username);
//
// String accessToken = jwtService.generateAccessToken(userLogin.getUsername(),
// userLogin);
// String refreshToken =
// jwtService.generateRefreshToken(userLogin.getUsername(), userLogin);
// }
//
// public void logoutUser() {
// }
// }

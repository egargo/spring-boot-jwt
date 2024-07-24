package io.egargo.jwt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.egargo.jwt.models.User;
import jakarta.servlet.http.HttpServletRequest;

/**
 * AuthStrategyImpl
 *
 * This class contains the implementation of AuthStrategy.
 */
@Service
public class AuthStrategyImpl implements AuthStrategy {
    @Autowired
    JwtService<String> jwtService = new JwtService<>();

    UserService userService = new UserService();

    /**
     * An example of protected route, requires authenticated and authorized user to
     * access.
     *
     * This checks the Authorization header (bearer token/access token).
     *
     * If there is no authorization header found, then the user that is trying to
     * access to route is not logged in.
     *
     * If there is an Authorization header found, check if the token is not expired.
     *
     * If there is no refreshToken found, then the user needs to refresh their
     * token.
     * If there is a refreshToken, verify that the token is not expired.
     */
    @Override
    public ResponseEntity<?> authorizationCheck(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("refreshToken");

        if (bearerToken == null || bearerToken.split(" ").length <= 0) {
            return new ResponseEntity<>("No authorization token found", HttpStatusCode.valueOf(400));
        }

        if (!jwtService.verifyToken(bearerToken.split(" ")[1])) {
            return new ResponseEntity<>("Expired token", HttpStatusCode.valueOf(400));
        }

        if (refreshToken == null) {
            return new ResponseEntity<>("No refresh token found",
                    HttpStatusCode.valueOf(400));
        }

        if (!jwtService.verifyToken(refreshToken)) {
            System.out.println("2");
            return new ResponseEntity<>("You are not authorized to access this resource",
                    HttpStatusCode.valueOf(401));
        }

        return new ResponseEntity<>(String.format("Authorization Header: " + bearerToken),
                HttpStatusCode.valueOf(200));

    }
}

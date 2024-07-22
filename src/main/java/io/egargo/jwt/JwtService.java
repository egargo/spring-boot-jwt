package io.egargo.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * This class accepts any object as the parameter
 */
@Service
public class JwtService<T> {
    private String secretKey = "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b";
    // Token expiration is 7 days.
    private long tokenExp = 60 * 60 * 24 * 7 * 1000;
    // Refresh token expiration is 15 minutes.
    private long tokenRefreshExp = 15 * 60 * 1000;
    private String refreshToken;
    private String token;
    private Claims claims;

    public JwtService() {
    }

    // Public methods

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    private void createToken(HashMap<String, T> claims, String username) {
        this.token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExp))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    private void createRefreshToken(HashMap<String, T> claims, String username) {
        this.refreshToken = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenRefreshExp))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(this.getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claim = extractAllClaims(token);
        return claimResolver.apply(claim);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Public methods

    public boolean verifyToken(String token, T data) {
        try {
            final boolean validity = !isTokenExpired(token);
            return validity;
        } catch (Exception e) {
            throw new ExpiredJwtException(null, claims, null);
        }
    }

    public boolean verifyRefreshToken(String token, T data) {
        try {
            final boolean validity = !isTokenExpired(token);
            return validity;
        } catch (Exception e) {
            throw new ExpiredJwtException(null, claims, null);
        }
    }

    public void generateToken(String username, T user) {
        HashMap<String, T> claims = new HashMap<>();
        claims.put("data", user);
        createToken(claims, username);
    }

    public void generateRefreshToken(String username, T user) {
        HashMap<String, T> claims = new HashMap<>();
        claims.put("data", user);
        createRefreshToken(claims, username);
    }

    // Getters and setters
    public String getToken() {
        return this.token;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public Claims getClaims() {
        return this.claims;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }
}

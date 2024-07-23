package io.egargo.jwt.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService<T> {
    @Value("${security.jwt.secret-key}")
    private String tokenSecretKey;

    @Value("${security.jwt.access-expiration-time}")
    private long tokenAccessExp;

    @Value("${security.jwt.refresh-expiration-time}")
    private long tokenRefreshExp;

    private String tokenAccess;
    private String tokenRefresh;
    private Claims claims;

    public JwtService() {
    }

    // Public methods

    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecretKey));
    }

    private String createAccessToken(HashMap<String, T> claims, String username) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenAccessExp))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    private String createRefreshToken(HashMap<String, T> claims, String username) {
        return Jwts
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

    public boolean verifyAccessToken(String token) {
        try {
            final boolean validity = !isTokenExpired(token);
            if (!validity) {
                return false;
            }
            return validity;
        } catch (Exception e) {
            throw new ExpiredJwtException(null, claims, null);
        }
    }

    public boolean verifyRefreshToken(String token) {
        try {
            final boolean validity = !isTokenExpired(token);
            if (!validity) {
                return false;
            }
            return validity;
        } catch (Exception e) {
            throw new ExpiredJwtException(null, claims, null);
        }
    }

    public String generateAccessToken(String username, T user) {
        HashMap<String, T> claims = new HashMap<>();
        claims.put("data", user);
        return createAccessToken(claims, username);
    }

    public String generateRefreshToken(String username, T user) {
        HashMap<String, T> claims = new HashMap<>();
        claims.put("data", user);
        return createRefreshToken(claims, username);
    }

    // Getters and setters
    public String getAccessToken() {
        return this.tokenAccess;
    }

    public String getRefreshToken() {
        return this.tokenRefresh;
    }

    public Claims getClaims() {
        return this.claims;
    }
}

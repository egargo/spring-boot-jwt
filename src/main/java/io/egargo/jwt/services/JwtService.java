package io.egargo.jwt.services;

import java.security.Key;
import java.util.Date;
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

    private Claims claims;

    public JwtService() {
    }

    /**
     * @return
     */
    private Key getSignedKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSecretKey));
    }

    /**
     * @param username
     * @return
     */
    private String createAccessToken(final String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenAccessExp))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * @param username
     * @return
     */
    private String createRefreshToken(final String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenRefreshExp))
                .signWith(getSignedKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * @param token
     * @return
     */
    public Claims extractAllClaims(final String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(this.getSignedKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token
     * @return
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * @param <T>
     * @param token
     * @param claimResolver
     * @return
     */
    @SuppressWarnings("hiding")
    private <T> T extractClaim(final String token, final Function<Claims, T> claimResolver) {
        final Claims claim = extractAllClaims(token);
        return claimResolver.apply(claim);
    }

    /**
     * Check if token is expired.
     *
     * @param token the JSONWebToken
     * @return boolean if the token is expired
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * @param token
     * @return
     */
    public boolean verifyToken(final String token) {
        try {
            final boolean validity = !isTokenExpired(token);
            return validity;
        } catch (final Exception e) {
            throw new ExpiredJwtException(null, claims, null);
        }
    }

    /**
     * @param username
     * @return
     */
    public String generateAccessToken(final String username) {
        return createAccessToken(username);
    }

    /**
     * @param username
     * @return
     */
    public String generateRefreshToken(final String username) {
        return createRefreshToken(username);
    }
}

package io.egargo.jwt.services;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

/**
 * AuthStrategy
 */
public interface AuthStrategy {
    public ResponseEntity<?> authorizationCheck(HttpServletRequest request);
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final Set<String> WEAK_SECRETS = Set.of(
        "secret", "password", "jwt", "myapp", "test", "123456",
        "local-dev-secret", "changeme", "default"
    );

    @Value("${api.jwt.secret}")
    private String secret;

    @Value("${api.jwt.expiration-ms:86400000}")
    private long expirationMs;

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must not be blank (api.jwt.secret)");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException(
                "JWT secret is too short (" + secret.length() + " chars) — minimum 32 required");
        }
        if (WEAK_SECRETS.contains(secret.toLowerCase())) {
            throw new IllegalStateException(
                "JWT secret matches a known weak value — set a strong JWT_SECRET environment variable");
        }
        if (secret.length() < 64) {
            log.warn("[SECURITY] JWT secret is only {} chars — recommend 64+ for HS256 strength", secret.length());
        } else {
            log.info("[SECURITY] JWT secret validated — length {} meets strength requirements", secret.length());
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserId(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return extractClaims(token).getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}

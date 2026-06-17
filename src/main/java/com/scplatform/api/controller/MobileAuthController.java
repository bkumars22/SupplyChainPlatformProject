/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.api.jwt.JwtUtil;
import com.scplatform.api.security.LoginRateLimiter;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and token management")
public class MobileAuthController {

    private static final Logger logger = LoggerFactory.getLogger(MobileAuthController.class);
    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    private final UsersRepository usersRepository;
    private final JwtUtil jwtUtil;
    private final LoginRateLimiter rateLimiter;

    public MobileAuthController(UsersRepository usersRepository,
                                JwtUtil jwtUtil,
                                LoginRateLimiter rateLimiter) {
        this.usersRepository = usersRepository;
        this.jwtUtil = jwtUtil;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/login")
    @Transactional
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {

        String clientIp = resolveClientIp(httpRequest);

        if (rateLimiter.isBlocked(clientIp)) {
            long retryAfter = rateLimiter.retryAfterSeconds(clientIp);
            logger.warn("Rate-limited login attempt from IP: {} — retry after {}s", clientIp, retryAfter);
            return ResponseEntity.status(429)
                    .header("Retry-After", String.valueOf(retryAfter))
                    .body(Map.of("error", "Too many login attempts. Please try again later.",
                                 "retryAfterSeconds", retryAfter));
        }

        if (request.username() == null || request.username().isBlank()
                || request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and password are required"));
        }

        List<Users> users = usersRepository.findAllByUserId(request.username().trim());

        if (users.size() != 1 || !users.get(0).getIsEnabled()) {
            rateLimiter.recordFailure(clientIp);
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        Users user = users.get(0);

        // Password check: if no password stored yet, accept any password (first login)
        // Once password is set, validate with BCrypt
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (!bcrypt.matches(request.password(), user.getPassword())) {
                rateLimiter.recordFailure(clientIp);
                logger.warn("Wrong password for user: {}", request.username());
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
        }

        rateLimiter.recordSuccess(clientIp);

        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";
        usersRepository.updateLastAccessDate(user.getUserKey(), new Timestamp(System.currentTimeMillis()));

        String token = jwtUtil.generateToken(user.getUserId(), roleName);
        logger.info("Login successful for userId: {}", user.getUserId());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getUserId(),
                "userName", user.getUserName() != null ? user.getUserName() : user.getUserId(),
                "role", roleName,
                "expiresIn", 86400
        ));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info from JWT token")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Missing or invalid token"));
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Token expired or invalid"));
        }
        String userId = jwtUtil.extractUserId(token);
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        Users user = users.get(0);
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : "USER";
        return ResponseEntity.ok(Map.of(
                "userId", user.getUserId(),
                "userName", user.getUserName() != null ? user.getUserName() : user.getUserId(),
                "email", user.getEmailAddress() != null ? user.getEmailAddress() : "",
                "role", roleName
        ));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    record LoginRequest(String username, String password) {}
}

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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Regression coverage for BB-AUTH-02 (P0): a user whose stored password
 * hash is null/blank must be REJECTED at login, never waved through.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MobileAuthControllerTest {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    @Mock private UsersRepository usersRepository;
    @Mock private JwtUtil jwtUtil;
    @Mock private LoginRateLimiter rateLimiter;

    private MobileAuthController controller;
    private HttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        controller = new MobileAuthController(usersRepository, jwtUtil, rateLimiter);
        httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(rateLimiter.isBlocked(anyString())).thenReturn(false);
    }

    private Users enabledUser(String password) {
        Users u = new Users();
        u.setUserId("alice");
        u.setUserName("Alice");
        u.setPassword(password);
        u.setIsEnabled(true);
        return u;
    }

    @Test
    void login_rejectsAnyPassword_whenStoredHashIsNull() {
        when(usersRepository.findAllByUserId("alice")).thenReturn(List.of(enabledUser(null)));

        ResponseEntity<?> response = controller.login(
                new MobileAuthController.LoginRequest("alice", "literally-any-password"), httpRequest);

        assertEquals(401, response.getStatusCode().value());
        assertEquals(Map.of("error", "Invalid credentials"), response.getBody());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
        verify(rateLimiter).recordFailure("127.0.0.1");
        verify(rateLimiter, never()).recordSuccess(anyString());
    }

    @Test
    void login_rejectsAnyPassword_whenStoredHashIsBlank() {
        when(usersRepository.findAllByUserId("alice")).thenReturn(List.of(enabledUser("")));

        ResponseEntity<?> response = controller.login(
                new MobileAuthController.LoginRequest("alice", "anything-at-all"), httpRequest);

        assertEquals(401, response.getStatusCode().value());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_succeeds_whenPasswordMatchesStoredBcryptHash() {
        String hash = BCRYPT.encode("correct-horse-battery-staple");
        when(usersRepository.findAllByUserId("alice")).thenReturn(List.of(enabledUser(hash)));
        when(jwtUtil.generateToken("alice", "USER")).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = controller.login(
                new MobileAuthController.LoginRequest("alice", "correct-horse-battery-staple"), httpRequest);

        assertEquals(200, response.getStatusCode().value());
        verify(rateLimiter).recordSuccess("127.0.0.1");
        verify(rateLimiter, never()).recordFailure(anyString());
    }

    @Test
    void login_rejects_whenPasswordDoesNotMatchStoredBcryptHash() {
        String hash = BCRYPT.encode("correct-horse-battery-staple");
        when(usersRepository.findAllByUserId("alice")).thenReturn(List.of(enabledUser(hash)));

        ResponseEntity<?> response = controller.login(
                new MobileAuthController.LoginRequest("alice", "wrong-password"), httpRequest);

        assertEquals(401, response.getStatusCode().value());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
        verify(rateLimiter).recordFailure("127.0.0.1");
    }
}

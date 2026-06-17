/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blocks brute-force login attacks.
 * Locks an IP for 15 minutes after 5 failed attempts.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000L;

    private final Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null) return false;
        if (record.lockedUntil != null && Instant.now().toEpochMilli() < record.lockedUntil) {
            return true;
        }
        if (record.lockedUntil != null) {
            attempts.remove(ip);
        }
        return false;
    }

    public void recordFailure(String ip) {
        AttemptRecord record = attempts.computeIfAbsent(ip, k -> new AttemptRecord());
        record.count++;
        if (record.count >= MAX_ATTEMPTS) {
            record.lockedUntil = Instant.now().toEpochMilli() + LOCK_DURATION_MS;
        }
    }

    public void recordSuccess(String ip) {
        attempts.remove(ip);
    }

    public long remainingLockMs(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null || record.lockedUntil == null) return 0;
        return Math.max(0, record.lockedUntil - Instant.now().toEpochMilli());
    }

    private static class AttemptRecord {
        int count = 0;
        Long lockedUntil = null;
    }
}

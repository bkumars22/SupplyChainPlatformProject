/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Two-tier brute-force protection on the login endpoint:
 *   Tier 1 — sliding window: max 5 attempts per IP per 60 seconds → HTTP 429
 *   Tier 2 — hard lockout: after 10 total failures, IP is blocked for 15 minutes
 */
@Component
public class LoginRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimiter.class);

    private static final int  WINDOW_MAX       = 5;
    private static final long WINDOW_MS        = 60_000L;
    private static final int  LOCKOUT_THRESHOLD = 10;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000L;

    private final Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    /** Returns true when the IP must be rejected (either tier). */
    public boolean isBlocked(String ip) {
        AttemptRecord rec = attempts.get(ip);
        if (rec == null) return false;

        // Tier 2: hard lockout
        if (rec.lockedUntil != null) {
            if (Instant.now().toEpochMilli() < rec.lockedUntil) return true;
            attempts.remove(ip);
            return false;
        }

        // Tier 1: sliding-window
        evictExpired(rec);
        if (rec.windowHits.size() >= WINDOW_MAX) {
            log.warn("[RATE-LIMIT] IP {} exceeded {} attempts in {}s window at {}",
                    ip, WINDOW_MAX, WINDOW_MS / 1000, Instant.now());
            return true;
        }
        return false;
    }

    public void recordFailure(String ip) {
        AttemptRecord rec = attempts.computeIfAbsent(ip, k -> new AttemptRecord());
        long now = Instant.now().toEpochMilli();
        rec.windowHits.addLast(now);
        rec.totalFailures++;

        if (rec.totalFailures >= LOCKOUT_THRESHOLD) {
            rec.lockedUntil = now + LOCK_DURATION_MS;
            log.warn("[RATE-LIMIT] IP {} hard-locked for 15 min after {} failures at {}",
                    ip, rec.totalFailures, Instant.now());
        }
    }

    public void recordSuccess(String ip) {
        attempts.remove(ip);
    }

    /** Seconds until the sliding-window or hard-lock resets (for Retry-After header). */
    public long retryAfterSeconds(String ip) {
        AttemptRecord rec = attempts.get(ip);
        if (rec == null) return 0;
        if (rec.lockedUntil != null) {
            return Math.max(1, (rec.lockedUntil - Instant.now().toEpochMilli()) / 1000);
        }
        // Sliding window: oldest hit expires after WINDOW_MS
        if (!rec.windowHits.isEmpty()) {
            long oldestHit = rec.windowHits.peekFirst();
            return Math.max(1, (oldestHit + WINDOW_MS - Instant.now().toEpochMilli()) / 1000);
        }
        return 0;
    }

    /** @deprecated use retryAfterSeconds — kept for any callers that used ms */
    public long remainingLockMs(String ip) {
        return retryAfterSeconds(ip) * 1000;
    }

    private void evictExpired(AttemptRecord rec) {
        long cutoff = Instant.now().toEpochMilli() - WINDOW_MS;
        while (!rec.windowHits.isEmpty() && rec.windowHits.peekFirst() < cutoff) {
            rec.windowHits.pollFirst();
        }
    }

    private static class AttemptRecord {
        final Deque<Long> windowHits = new ArrayDeque<>();
        int totalFailures = 0;
        Long lockedUntil  = null;
    }
}

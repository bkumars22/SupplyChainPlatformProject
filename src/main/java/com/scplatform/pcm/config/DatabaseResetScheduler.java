/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Nightly scheduled reset of the demo database back to clean seed data.
 *
 * Cron default: "0 30 18 * * *" = 18:30 UTC = midnight IST (UTC+5:30).
 * Override via DEMO_RESET_CRON environment variable.
 * Disable via DEMO_RESET_ENABLED=false.
 */
@Component
public class DatabaseResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DatabaseResetScheduler.class);

    private final DatabaseSeeder seeder;

    @Value("${demo.auto-reset.enabled:true}")
    private boolean enabled;

    public DatabaseResetScheduler(DatabaseSeeder seeder) {
        this.seeder = seeder;
    }

    @Scheduled(cron = "${demo.auto-reset.cron:0 30 18 * * *}")
    public void scheduledReset() {
        if (!enabled) {
            log.debug("Scheduled demo reset skipped — demo.auto-reset.enabled=false");
            return;
        }
        long start = System.currentTimeMillis();
        log.info("Scheduled demo database reset starting at {}", LocalDateTime.now());
        try {
            seeder.resetToSeedData();
            log.info("Scheduled demo database reset completed in {}ms",
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("Scheduled demo database reset failed after {}ms: {}",
                    System.currentTimeMillis() - start, e.getMessage(), e);
        }
    }
}

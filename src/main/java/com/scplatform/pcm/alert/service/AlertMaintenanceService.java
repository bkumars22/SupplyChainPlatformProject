/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Scheduled maintenance tasks for the alert system:
 * <ul>
 *   <li>Deletes expired alerts from SC_ALERT_DETAIL daily at 2 AM</li>
 *   <li>Cleans up old resolved DLQ entries from SC_ALERT_DLQ weekly</li>
 * </ul>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertMaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(AlertMaintenanceService.class);

    private final AlertDetailRepository alertDetailRepository;
    private final DlqAlertService dlqAlertService;

    @Value("${pcm.alert.dlq.cleanup-days:90}")
    private int dlqCleanupDays;

    public AlertMaintenanceService(AlertDetailRepository alertDetailRepository,
                                   DlqAlertService dlqAlertService) {
        this.alertDetailRepository = alertDetailRepository;
        this.dlqAlertService = dlqAlertService;
    }

    /** Runs daily at 2:00 AM to delete expired alerts from the detail table. */
    @Scheduled(cron = "${pcm.alert.cleanup.cron:0 0 2 * * ?}")
    @Transactional
    public void cleanupExpiredAlerts() {
        log.info("Starting expired alert cleanup...");
        try {
            int deleted = alertDetailRepository.deleteExpiredAlerts(LocalDate.now());
            log.info("Expired alert cleanup complete. Deleted {} alerts.", deleted);
        } catch (Exception e) {
            log.error("Error during expired alert cleanup", e);
        }
    }

    /**
     * Runs weekly (Sunday at 3:00 AM) to clean up old resolved DLQ entries.
     * Entries that have been resolved for longer than {@code pcm.alert.dlq.cleanup-days}
     * (default 90 days) are permanently deleted.
     */
    @Scheduled(cron = "${pcm.alert.dlq.cleanup.cron:0 0 3 ? * SUN}")
    @Transactional
    public void cleanupResolvedDlqEntries() {
        log.info("Starting resolved DLQ entry cleanup (older than {} days)...", dlqCleanupDays);
        try {
            int deleted = dlqAlertService.cleanupOldEntries(dlqCleanupDays);
            log.info("DLQ cleanup complete. Deleted {} resolved entries.", deleted);
        } catch (Exception e) {
            log.error("Error during DLQ entry cleanup", e);
        }
    }

    /**
     * Logs the current DLQ count at startup and periodically for monitoring.
     * Runs every hour.
     */
    @Scheduled(fixedDelayString = "${pcm.alert.dlq.monitor-interval:3600000}")
    public void monitorDlqDepth() {
        try {
            long unresolvedCount = dlqAlertService.getUnresolvedCount();
            if (unresolvedCount > 0) {
                log.warn("ALERT DLQ MONITOR: {} unresolved DLQ entries require attention", unresolvedCount);
            } else {
                log.debug("DLQ monitor: 0 unresolved entries");
            }
        } catch (Exception e) {
            log.error("Error checking DLQ depth", e);
        }
    }
}

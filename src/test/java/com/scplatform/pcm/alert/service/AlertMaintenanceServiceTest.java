/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertMaintenanceServiceTest {

    @Mock private AlertDetailRepository alertDetailRepository;
    @Mock private DlqAlertService dlqAlertService;

    @InjectMocks private AlertMaintenanceService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "dlqCleanupDays", 90);
    }

    // ── cleanupExpiredAlerts ──

    @Test
    void cleanupExpiredAlerts_callsRepositoryWithToday() {
        when(alertDetailRepository.deleteExpiredAlerts(any(LocalDate.class))).thenReturn(7);
        service.cleanupExpiredAlerts();
        verify(alertDetailRepository).deleteExpiredAlerts(any(LocalDate.class));
    }

    @Test
    void cleanupExpiredAlerts_swallowsRepositoryException() {
        when(alertDetailRepository.deleteExpiredAlerts(any(LocalDate.class)))
                .thenThrow(new RuntimeException("db down"));
        // should not propagate
        service.cleanupExpiredAlerts();
        verify(alertDetailRepository).deleteExpiredAlerts(any(LocalDate.class));
    }

    // ── cleanupResolvedDlqEntries ──

    @Test
    void cleanupResolvedDlqEntries_delegatesToDlqService_withConfiguredDays() {
        when(dlqAlertService.cleanupOldEntries(90)).thenReturn(3);
        service.cleanupResolvedDlqEntries();
        verify(dlqAlertService).cleanupOldEntries(90);
    }

    @Test
    void cleanupResolvedDlqEntries_swallowsException() {
        when(dlqAlertService.cleanupOldEntries(anyInt())).thenThrow(new RuntimeException("boom"));
        service.cleanupResolvedDlqEntries();
        verify(dlqAlertService).cleanupOldEntries(90);
    }

    // ── monitorDlqDepth ──

    @Test
    void monitorDlqDepth_logsWarn_whenUnresolvedGreaterThanZero() {
        when(dlqAlertService.getUnresolvedCount()).thenReturn(5L);
        service.monitorDlqDepth();
        verify(dlqAlertService).getUnresolvedCount();
    }

    @Test
    void monitorDlqDepth_logsDebug_whenUnresolvedIsZero() {
        when(dlqAlertService.getUnresolvedCount()).thenReturn(0L);
        service.monitorDlqDepth();
        verify(dlqAlertService).getUnresolvedCount();
    }

    @Test
    void monitorDlqDepth_swallowsException() {
        when(dlqAlertService.getUnresolvedCount()).thenThrow(new RuntimeException("oops"));
        service.monitorDlqDepth();
        verify(dlqAlertService).getUnresolvedCount();
    }
}

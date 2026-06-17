/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.Service;

import com.scplatform.pcm.audit.repository.PcmAuditHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class PcmAuditHistoryServiceTest {

    @Mock private PcmAuditHistoryRepository repository;
    @InjectMocks private PcmAuditHistoryService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getStartYear_delegatesToRepository() {
        when(repository.getStartYear()).thenReturn(2020);
        assertEquals(2020, service.getStartYear());
    }

    @Test
    void getStartYear_returnsNullWhenRepositoryReturnsNull() {
        when(repository.getStartYear()).thenReturn(null);
        assertNull(service.getStartYear());
    }
}

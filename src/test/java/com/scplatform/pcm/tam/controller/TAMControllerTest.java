/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.tam.service.TAMAllocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TAMControllerTest {

    @Mock private TAMAllocationService tamAllocationService;
    @InjectMocks private TAMController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkTAMAllocationExist_returnsTrue_whenAllocationExists() {
        when(tamAllocationService.checkIfTAMExistsForFunctionalGroupAndSite(10L, 20L)).thenReturn(true);

        boolean result = controller.checkTAMAllocationExist(10L, 20L);

        assertTrue(result);
        verify(tamAllocationService).checkIfTAMExistsForFunctionalGroupAndSite(10L, 20L);
    }

    @Test
    void checkTAMAllocationExist_returnsFalse_whenNoAllocationExists() {
        when(tamAllocationService.checkIfTAMExistsForFunctionalGroupAndSite(5L, 8L)).thenReturn(false);

        boolean result = controller.checkTAMAllocationExist(5L, 8L);

        assertFalse(result);
        verify(tamAllocationService).checkIfTAMExistsForFunctionalGroupAndSite(5L, 8L);
    }

    @Test
    void checkTAMAllocationExist_throwsRuntimeException_whenServiceThrows() {
        when(tamAllocationService.checkIfTAMExistsForFunctionalGroupAndSite(1L, 2L))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> controller.checkTAMAllocationExist(1L, 2L));

        assertEquals("Failed to check TAM allocation existence", ex.getMessage());
        verify(tamAllocationService).checkIfTAMExistsForFunctionalGroupAndSite(1L, 2L);
    }
}

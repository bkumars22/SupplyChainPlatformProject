/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository markers without methods or generic JpaRepository binding.
 * These exist as Spring-managed beans for symmetry / future use; tests verify the contract.
 */
class CostMarkerRepositoriesTest {

    @Test
    void testPcmCostRecordAssignmentRepository_isMarkerRepository() {
        assertNotNull(PcmCostRecordAssignmentRepository.class.getAnnotation(Repository.class));
        assertEquals(0, PcmCostRecordAssignmentRepository.class.getDeclaredMethods().length);
        assertTrue(PcmCostRecordAssignmentRepository.class.isInterface());
    }

    @Test
    void testPcmCostRecordRangeRepository_isMarkerRepository() {
        assertNotNull(PcmCostRecordRangeRepository.class.getAnnotation(Repository.class));
        assertEquals(0, PcmCostRecordRangeRepository.class.getDeclaredMethods().length);
        assertTrue(PcmCostRecordRangeRepository.class.isInterface());
    }

    @Test
    void testPcmCostRecordExceptionRepository_isMarkerRepository() {
        assertNotNull(PcmCostRecordExceptionRepository.class.getAnnotation(Repository.class));
        assertEquals(0, PcmCostRecordExceptionRepository.class.getDeclaredMethods().length);
        assertTrue(PcmCostRecordExceptionRepository.class.isInterface());
    }

    @Test
    void testPcmCostRecordRangeExceptionRepository_isMarkerRepository() {
        assertNotNull(PcmCostRecordRangeExceptionRepository.class.getAnnotation(Repository.class));
        assertEquals(0, PcmCostRecordRangeExceptionRepository.class.getDeclaredMethods().length);
        assertTrue(PcmCostRecordRangeExceptionRepository.class.isInterface());
    }

    @Test
    void testPcmCostRecordValueExceptionRepository_isMarkerRepository() {
        assertNotNull(PcmCostRecordValueExceptionRepository.class.getAnnotation(Repository.class));
        assertEquals(0, PcmCostRecordValueExceptionRepository.class.getDeclaredMethods().length);
        assertTrue(PcmCostRecordValueExceptionRepository.class.isInterface());
    }
}

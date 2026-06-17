/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PcmAuditHistoryRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmAuditHistoryRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(PcmAuditHistoryRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void getStartYear_isNativeQuery() throws NoSuchMethodException {
        Method m = PcmAuditHistoryRepository.class.getMethod("getStartYear");
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.nativeQuery());
        assertTrue(q.value().contains("MIN(ACTION_DATE)"));
        assertEquals(Integer.class, m.getReturnType());
    }
}

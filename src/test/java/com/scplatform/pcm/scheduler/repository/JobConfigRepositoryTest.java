/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.scheduler.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class JobConfigRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(JobConfigRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(JobConfigRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findByEnabledTrue_isDeclared() throws NoSuchMethodException {
        Method m = JobConfigRepository.class.getMethod("findByEnabledTrue");
        assertEquals(java.util.List.class, m.getReturnType());
    }
}

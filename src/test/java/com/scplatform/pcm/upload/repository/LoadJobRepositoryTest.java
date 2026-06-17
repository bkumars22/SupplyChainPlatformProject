/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.upload.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

class LoadJobRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(LoadJobRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(LoadJobRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void hasFinderMethods() throws NoSuchMethodException {
        assertNotNull(LoadJobRepository.class.getMethod("findByExternalId", String.class));
        assertNotNull(LoadJobRepository.class.getMethod("findWithEventsByLoadJobKey", String.class));
    }
}

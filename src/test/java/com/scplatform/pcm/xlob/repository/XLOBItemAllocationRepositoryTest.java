/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.xlob.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XLOBItemAllocationRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(XLOBItemAllocationRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(XLOBItemAllocationRepository.class.getAnnotation(Repository.class));
    }
}

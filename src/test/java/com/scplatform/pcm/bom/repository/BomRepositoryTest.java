/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

class BomRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(BomRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(BomRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void hasFinderMethods() throws NoSuchMethodException {
        assertNotNull(BomRepository.class.getMethod("findByBomExternalId", String.class));
        assertNotNull(BomRepository.class.getMethod("findByBomName", String.class));
        assertNotNull(BomRepository.class.getMethod("findByStatus", String.class));
        assertNotNull(BomRepository.class.getMethod("findByBusinessEntityKey", Long.class));
        assertNotNull(BomRepository.class.getMethod("findByItemKey", Long.class));
        assertNotNull(BomRepository.class.getMethod("findByIsTopLevel", Boolean.class));
        assertNotNull(BomRepository.class.getMethod("findByDataSource", String.class));
        assertNotNull(BomRepository.class.getMethod("findByTypeCode", String.class));
        assertNotNull(BomRepository.class.getMethod("findByStatusAndBusinessEntity", String.class, Long.class));
        assertNotNull(BomRepository.class.getMethod("countByStatus", String.class));
        assertNotNull(BomRepository.class.getMethod("existsByBomExternalId", String.class));
        Method m = BomRepository.class.getMethod("findBomsForItem", Long.class, Date.class, List.class);
        assertNotNull(m);
        assertNotNull(BomRepository.class.getMethod("findBomStatus", List.class, Date.class));
        assertNotNull(BomRepository.class.getMethod("findBomStatusForOwner", List.class, Date.class, String.class));
    }
}

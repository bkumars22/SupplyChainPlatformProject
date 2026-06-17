/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

class BomGroupLinkRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(BomGroupLinkRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(BomGroupLinkRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void hasFinderMethods() throws NoSuchMethodException {
        Method m1 = BomGroupLinkRepository.class.getMethod("findByBom", com.scplatform.pcm.bom.entity.Bom.class);
        Method m2 = BomGroupLinkRepository.class.getMethod("findByBomGroup", com.scplatform.pcm.bom.entity.BomGroup.class);
        Method m3 = BomGroupLinkRepository.class.getMethod("findByBomAndBomGroup",
                com.scplatform.pcm.bom.entity.Bom.class, com.scplatform.pcm.bom.entity.BomGroup.class);
        Method m4 = BomGroupLinkRepository.class.getMethod("existsByBomAndBomGroup",
                com.scplatform.pcm.bom.entity.Bom.class, com.scplatform.pcm.bom.entity.BomGroup.class);
        assertNotNull(m1);
        assertNotNull(m2);
        assertNotNull(m3);
        assertNotNull(m4);
    }
}

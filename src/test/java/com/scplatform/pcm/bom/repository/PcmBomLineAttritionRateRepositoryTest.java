/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.item.entity.Item;

class PcmBomLineAttritionRateRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmBomLineAttritionRateRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(PcmBomLineAttritionRateRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void hasFinderMethod() throws NoSuchMethodException {
        assertNotNull(PcmBomLineAttritionRateRepository.class.getMethod(
                "findByIdBomLineAndIdBomItem", BomLine.class, Item.class));
    }
}

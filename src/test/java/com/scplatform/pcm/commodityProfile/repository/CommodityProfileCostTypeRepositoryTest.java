/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.commodityProfile.entity.CommodityProfileCostType;

class CommodityProfileCostTypeRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CommodityProfileCostTypeRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(CommodityProfileCostTypeRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void entityTypeIsCommodityProfileCostType() {
        assertNotNull(CommodityProfileCostType.class);
    }
}

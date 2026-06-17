/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmCostType;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostTypeRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmCostTypeRepository.class));
        assertNotNull(PcmCostTypeRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testAllCustomMethodsHaveQueryAnnotation() throws Exception {
        Method[] methods = {
                PcmCostTypeRepository.class.getMethod("getAllCostTypes"),
                PcmCostTypeRepository.class.getMethod("getAllItemCategoryCostTypes"),
                PcmCostTypeRepository.class.getMethod("getAllRollupCostTypes"),
                PcmCostTypeRepository.class.getMethod("getCostType", String.class),
                PcmCostTypeRepository.class.getMethod("getCostTypesKey")
        };
        for (Method m : methods) {
            assertNotNull(m.getAnnotation(Query.class), "missing @Query on " + m.getName());
        }
        assertTrue(PcmCostTypeRepository.class.getMethod("getAllItemCategoryCostTypes")
                .getAnnotation(Query.class).value().contains("useInItemCategoryCost"));
        assertTrue(PcmCostTypeRepository.class.getMethod("getAllRollupCostTypes")
                .getAnnotation(Query.class).value().contains("useInRollup"));
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmPricingScenario;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PcmPricingScenarioRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmPricingScenarioRepository.class));
        assertNotNull(PcmPricingScenarioRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testQueryAnnotations() throws Exception {
        Method getAll = PcmPricingScenarioRepository.class.getMethod("getAllPricingScenarios");
        Method byName = PcmPricingScenarioRepository.class.getMethod("getPricingScenarioByName", String.class);
        assertNotNull(getAll.getAnnotation(Query.class));
        assertNotNull(byName.getAnnotation(Query.class));
        // case-insensitive name lookup
        assertTrue(byName.getAnnotation(Query.class).value().contains("LOWER"));
    }
}

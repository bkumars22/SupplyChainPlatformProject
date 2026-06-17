/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.repo;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.junit.jupiter.api.Assertions.*;

class PcmForecastRepositoryTest {

    @Test
    void testExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmForecastRepository.class));
    }

    @Test
    void testIsAnnotatedWithRepository() {
        assertNotNull(PcmForecastRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testGenericTypeParameters() {
        // JpaRepository<PcmForecast, Long> — verify interface is parameterized correctly
        // by checking that PcmForecast is the declared entity
        java.lang.reflect.Type[] genericInterfaces =
                PcmForecastRepository.class.getGenericInterfaces();
        assertTrue(genericInterfaces.length > 0,
                "PcmForecastRepository must extend at least one interface");
        String genericStr = genericInterfaces[0].getTypeName();
        assertTrue(genericStr.contains("PcmForecast"),
                "Generic type should include PcmForecast");
        assertTrue(genericStr.contains("Long"),
                "Generic type should include Long as the ID type");
    }

    @Test
    void testIsInterface() {
        assertTrue(PcmForecastRepository.class.isInterface());
    }
}

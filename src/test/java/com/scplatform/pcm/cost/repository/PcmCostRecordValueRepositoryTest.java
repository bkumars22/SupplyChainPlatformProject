/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmCostRecordValue;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostRecordValueRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmCostRecordValueRepository.class));
        assertNotNull(PcmCostRecordValueRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testGenericTypeBindings() {
        ParameterizedType pt = (ParameterizedType) PcmCostRecordValueRepository.class.getGenericInterfaces()[0];
        Type[] args = pt.getActualTypeArguments();
        assertEquals(PcmCostRecordValue.class, args[0]);
        assertEquals(Long.class, args[1]);
    }

    @Test
    void testNoDeclaredMethods() {
        // Marker repository — relies entirely on inherited JpaRepository CRUD.
        assertEquals(0, PcmCostRecordValueRepository.class.getDeclaredMethods().length);
    }
}

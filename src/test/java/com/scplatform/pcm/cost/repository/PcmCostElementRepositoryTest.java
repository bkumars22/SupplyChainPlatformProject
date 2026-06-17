/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.entity.PcmCostElementId;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.junit.jupiter.api.Assertions.*;

class PcmCostElementRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmCostElementRepository.class));
        assertNotNull(PcmCostElementRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testGenericTypeBindings() {
        ParameterizedType pt = (ParameterizedType) PcmCostElementRepository.class.getGenericInterfaces()[0];
        Type[] args = pt.getActualTypeArguments();
        assertEquals(PcmCostElement.class, args[0]);
        assertEquals(PcmCostElementId.class, args[1]);
    }

    @Test
    void testAllDeclaredMethodsAreAnnotatedWithQuery() throws Exception {
        Method getCostElement = PcmCostElementRepository.class.getMethod(
                "getCostElement", String.class, String.class);
        Method getAllCostElements = PcmCostElementRepository.class.getMethod("getAllCostElements");
        assertNotNull(getCostElement.getAnnotation(Query.class));
        assertNotNull(getAllCostElements.getAnnotation(Query.class));
        assertTrue(getCostElement.getAnnotation(Query.class).value().contains("PcmCostElement"));
        assertTrue(getAllCostElements.getAnnotation(Query.class).value().contains("displayOrder"));
    }
}

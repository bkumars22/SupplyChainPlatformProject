/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.userAlert.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserAlertRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(UserAlertRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(UserAlertRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findAllByOrderByAlertDateDesc_methodPresent() throws NoSuchMethodException {
        Method m = UserAlertRepository.class.getMethod("findAllByOrderByAlertDateDesc");
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void findByAlertFilter_methodPresent() throws NoSuchMethodException {
        Method m = UserAlertRepository.class.getMethod("findByAlertFilter", String.class);
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void findByAlertFilters_hasQueryAnnotation() throws NoSuchMethodException {
        Method m = UserAlertRepository.class.getMethod("findByAlertFilters", List.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("UserAlert"));
        assertTrue(q.value().contains("ORDER BY"));
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.contact.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(ContactRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(ContactRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findByStatus_methodPresent() throws NoSuchMethodException {
        Method m = ContactRepository.class.getMethod("findByStatus", String.class);
        assertEquals(List.class, m.getReturnType());
    }
}

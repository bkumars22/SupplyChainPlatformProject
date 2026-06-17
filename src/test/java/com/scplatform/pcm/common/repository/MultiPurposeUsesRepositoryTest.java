/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

class MultiPurposeUsesRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(MultiPurposeUsesRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(MultiPurposeUsesRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void hasExpectedQueryMethods() throws NoSuchMethodException {
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "getAllMultiPurposeList", long.class, String.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "getAllDisplay", long.class, String.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "findByUserIdAndDisplayId", long.class, long.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "findDefaultDisplay", long.class, String.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "updateDefaultDisplay", long.class, String.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "getAvailableColumn", long.class, String.class, String.class));
        assertNotNull(MultiPurposeUsesRepository.class.getMethod(
                "checkDisplayAlreadyExist", long.class, String.class, String.class, String.class));
    }
}

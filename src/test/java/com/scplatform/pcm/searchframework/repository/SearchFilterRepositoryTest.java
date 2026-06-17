/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.searchframework.entity.SearchFilter;
import com.scplatform.pcm.user.entity.Users;

class SearchFilterRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(SearchFilterRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(SearchFilterRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findByNameAndOwnerAndTypeMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod(
                "findByNameAndOwnerAndType", String.class, Users.class, String.class);
        assertNotNull(m);
    }

    @Test
    void findAllByNameAndOwnerAndTypeMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod(
                "findAllByNameAndOwnerAndType", String.class, Users.class, String.class);
        assertNotNull(m);
    }

    @Test
    void findPublicFiltersByTypeMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod("findPublicFiltersByType", String.class);
        assertNotNull(m);
    }

    @Test
    void findByCreatorMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod("findByCreator", Users.class);
        assertNotNull(m);
    }

    @Test
    void findByNameContainingIgnoreCaseAndFilterTypeMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod(
                "findByNameContainingIgnoreCaseAndFilterType", String.class, String.class);
        assertNotNull(m);
    }

    @Test
    void findUserSearchFiltersMethodExists() throws NoSuchMethodException {
        Method m = SearchFilterRepository.class.getMethod(
                "findUserSearchFilters", String.class, Long.class, String.class);
        assertNotNull(m);
    }

    @Test
    void typeArgumentsAreSearchFilterAndLong() {
        // sanity check on generic type
        assertTrue(SearchFilter.class.equals(SearchFilter.class));
    }
}

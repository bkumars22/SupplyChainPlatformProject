/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.repository;

import com.scplatform.pcm.costexception.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Structural tests for all costexception JPA repository interfaces.
 * No Spring context or database required — tests verify:
 *  - Interface hierarchy (extends JpaRepository)
 *  - Custom query method signatures present in CostExceptionRepository
 *  - Marker interfaces are empty (no additional abstract methods)
 */
class CostExceptionRepositoriesTest {

    // -------- CostExceptionRepository ----------------------------------------

    @Test
    void costExceptionRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionRepository.class));
    }

    @Test
    void costExceptionRepository_hasFindByExceptionId() throws Exception {
        Method m = CostExceptionRepository.class.getMethod("findByExceptionId", String.class);
        assertEquals(Optional.class, m.getReturnType());
    }

    @Test
    void costExceptionRepository_hasFindByExceptionNameIgnoreCase() throws Exception {
        Method m = CostExceptionRepository.class.getMethod("findByExceptionNameIgnoreCase", String.class);
        assertEquals(Optional.class, m.getReturnType());
    }

    @Test
    void costExceptionRepository_typeParameterIsCostException() {
        java.lang.reflect.ParameterizedType pt =
                (java.lang.reflect.ParameterizedType) CostExceptionRepository.class.getGenericInterfaces()[0];
        assertEquals(CostException.class, pt.getActualTypeArguments()[0]);
    }

    // -------- CostExceptionApproverRepository --------------------------------

    @Test
    void approverRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionApproverRepository.class));
    }

    @Test
    void approverRepository_noCustomMethods() {
        Method[] declared = CostExceptionApproverRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length, "Marker interface should declare no methods");
    }

    @Test
    void approverRepository_typeParameterIsCostExceptionApprover() {
        java.lang.reflect.ParameterizedType pt =
                (java.lang.reflect.ParameterizedType) CostExceptionApproverRepository.class.getGenericInterfaces()[0];
        assertEquals(CostExceptionApprover.class, pt.getActualTypeArguments()[0]);
    }

    // -------- CostExceptionInfoRepository ------------------------------------

    @Test
    void infoRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionInfoRepository.class));
    }

    @Test
    void infoRepository_noCustomMethods() {
        Method[] declared = CostExceptionInfoRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length);
    }

    // -------- CostExceptionLOBRepository -------------------------------------

    @Test
    void lobRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionLOBRepository.class));
    }

    @Test
    void lobRepository_noCustomMethods() {
        Method[] declared = CostExceptionLOBRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length);
    }

    // -------- CostExceptionODMCMRepository -----------------------------------

    @Test
    void odmCmRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionODMCMRepository.class));
    }

    @Test
    void odmCmRepository_noCustomMethods() {
        Method[] declared = CostExceptionODMCMRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length);
    }

    // -------- CostExceptionODMEmailRepository --------------------------------

    @Test
    void odmEmailRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionODMEmailRepository.class));
    }

    @Test
    void odmEmailRepository_noCustomMethods() {
        Method[] declared = CostExceptionODMEmailRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length);
    }

    // -------- CostExceptionPricingRepository ---------------------------------

    @Test
    void pricingRepository_extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(CostExceptionPricingRepository.class));
    }

    @Test
    void pricingRepository_noCustomMethods() {
        Method[] declared = CostExceptionPricingRepository.class.getDeclaredMethods();
        assertEquals(0, declared.length);
    }
}

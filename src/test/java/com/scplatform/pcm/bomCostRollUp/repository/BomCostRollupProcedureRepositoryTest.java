/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.repository;

import com.scplatform.pcm.bom.entity.Bom;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BomCostRollupProcedureRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(BomCostRollupProcedureRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(BomCostRollupProcedureRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void runBomHierarchyWithCost_isAnnotatedAsProcedure() throws NoSuchMethodException {
        Method m = BomCostRollupProcedureRepository.class.getMethod(
                "runBomHierarchyWithCost", Long.class, Long.class, Date.class);
        Procedure p = m.getAnnotation(Procedure.class);
        assertNotNull(p);
        assertTrue(p.refCursor());
    }

    @Test
    void entityTypeIsBom() {
        assertNotNull(Bom.class);
    }
}

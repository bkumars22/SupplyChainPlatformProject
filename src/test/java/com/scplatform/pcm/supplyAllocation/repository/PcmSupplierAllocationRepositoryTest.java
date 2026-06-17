/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.supplyAllocation.repository;

import com.scplatform.pcm.supplyAllocation.entity.PcmSupplierAllocation;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PcmSupplierAllocationRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmSupplierAllocationRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(PcmSupplierAllocationRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void allKeyDefaultMethods_arePresent() {
        String[] expected = {
                "findSupplierAllocationByNaturalKey",
                "findSupplierAllocationForDate",
                "findType2SupplierAllocationsByItem",
                "getType2TotalSupplierAllocationsForItemPeriod",
                "findType2SupplierAllocationsByItemWithDestinationSite",
                "findType2SupplierAllocationByItem",
                "findType2SupplierAllocationPeriodsForItem",
                "findSupplierAllocationBetweenDates",
                "getSupplierAllocation",
                "deleteSupplierAllocationsByKey",
                "saveOrUpdate"
        };
        Method[] methods = PcmSupplierAllocationRepository.class.getMethods();
        for (String name : expected) {
            boolean found = Arrays.stream(methods).anyMatch(m -> m.getName().equals(name));
            assertTrue(found, "missing method: " + name);
        }
    }

    @Test
    void entityTypeIsPcmSupplierAllocation() {
        assertNotNull(PcmSupplierAllocation.class);
    }
}

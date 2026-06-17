/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.commodityProfile.entity.RoleCommodityProfileMapping;

class RoleCommodityProfileMappingRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(RoleCommodityProfileMappingRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(RoleCommodityProfileMappingRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void requiredMethodsArePresent() {
        String[] expected = {"deleteByCommodityProfile_ProfileIdIn", "deleteRoleProfileMapping"};
        Method[] methods = RoleCommodityProfileMappingRepository.class.getMethods();
        for (String name : expected) {
            boolean found = Arrays.stream(methods).anyMatch(m -> m.getName().equals(name));
            assertTrue(found, "missing method: " + name);
        }
    }

    @Test
    void entityTypeIsRoleCommodityProfileMapping() {
        assertNotNull(RoleCommodityProfileMapping.class);
    }
}

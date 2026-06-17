/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmCostRecordValue;
import com.scplatform.pcm.cost.entity.PcmCostValueDetail;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PcmCostValueDetailRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmCostValueDetailRepository.class));
        assertNotNull(PcmCostValueDetailRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testFindByNaturalKeyAnnotatedAndCallable() throws Exception {
        Method m = PcmCostValueDetailRepository.class.getMethod(
                "findCostValueDetailByNaturalKey", PcmCostRecordValue.class, String.class);
        assertNotNull(m.getAnnotation(Query.class));
        assertTrue(m.getAnnotation(Query.class).value().contains("costValueName"));

        PcmCostValueDetailRepository repo = mock(PcmCostValueDetailRepository.class);
        PcmCostRecordValue crv = mock(PcmCostRecordValue.class);
        PcmCostValueDetail d = new PcmCostValueDetail();
        when(repo.findCostValueDetailByNaturalKey(eq(crv), eq("X"))).thenReturn(Optional.of(d));
        assertSame(d, repo.findCostValueDetailByNaturalKey(crv, "X").orElseThrow());
    }
}

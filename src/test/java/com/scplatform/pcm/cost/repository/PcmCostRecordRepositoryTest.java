/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PcmCostRecordRepositoryTest {

    @Test
    void testStructure() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmCostRecordRepository.class));
        assertNotNull(PcmCostRecordRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testNullSiteConstant() {
        assertNotNull(PcmCostRecordRepository.NULLSITE);
        assertEquals(-9999L, PcmCostRecordRepository.NULLSITE.getSiteKey());
    }

    @Test
    void testGetCostRecord_DelegatesToFindByIdPresent() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        PcmCostRecord cr = new PcmCostRecord();
        when(repo.findById(10L)).thenReturn(Optional.of(cr));
        when(repo.getCostRecord(10L)).thenCallRealMethod();
        assertSame(cr, repo.getCostRecord(10L));
    }

    @Test
    void testGetCostRecord_ReturnsNullForAbsent() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        when(repo.findById(99L)).thenReturn(Optional.empty());
        when(repo.getCostRecord(99L)).thenCallRealMethod();
        assertNull(repo.getCostRecord(99L));
    }

    @Test
    void testSaveOrUpdate_DelegatesToSave() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        PcmCostRecord cr = new PcmCostRecord();
        when(repo.save(cr)).thenReturn(cr);
        when(repo.saveOrUpdate(cr)).thenCallRealMethod();
        assertSame(cr, repo.saveOrUpdate(cr));
        verify(repo).save(cr);
    }

    @Test
    void testFindByNaturalKey_7Arg_DelegatesWithNullPricingScenario() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        PcmSourcingLane lane = new PcmSourcingLane();
        PcmCostType ct = new PcmCostType();
        BusinessEntity provider = mock(BusinessEntity.class);
        Date from = new Date();
        Date to = new Date();
        List<PcmCostRecord> out = Collections.singletonList(new PcmCostRecord());

        // 9-arg overload (with pricingScenario + fgId) is invoked from the 8-arg default that passes null
        when(repo.findCostRecordByNaturalKey(
                eq(lane), eq(ct), eq(provider), eq("OPEN"),
                eq(from), eq(to), isNull(), eq("MPN-1"), eq(42)))
                .thenReturn(out);
        when(repo.findCostRecordByNaturalKey(
                eq(lane), eq(ct), eq(provider), eq("OPEN"),
                eq(from), eq(to), eq("MPN-1"), eq(42)))
                .thenCallRealMethod();

        assertSame(out, repo.findCostRecordByNaturalKey(
                lane, ct, provider, "OPEN", from, to, "MPN-1", 42));
    }

    @Test
    void testFindByNaturalKey_7ArgNoFg_DelegatesToEightArgWithNullPricingScenario() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        PcmSourcingLane lane = new PcmSourcingLane();
        PcmCostType ct = new PcmCostType();
        BusinessEntity provider = mock(BusinessEntity.class);
        Date from = new Date();
        Date to = new Date();
        List<PcmCostRecord> out = Collections.singletonList(new PcmCostRecord());

        // 7-arg-no-fgId default delegates to the 8-arg @Query overload (with PcmPricingScenario)
        // passing null for pricingScenario.
        when(repo.findCostRecordByNaturalKey(
                eq(lane), eq(ct), eq(provider), eq("S"),
                eq(from), eq(to), isNull(), eq("M")))
                .thenReturn(out);
        when(repo.findCostRecordByNaturalKey(
                eq(lane), eq(ct), eq(provider), eq("S"),
                eq(from), eq(to), eq("M")))
                .thenCallRealMethod();

        assertSame(out, repo.findCostRecordByNaturalKey(
                lane, ct, provider, "S", from, to, "M"));
    }

    @Test
    void testFindCostRecordByExternalIdReachable() {
        PcmCostRecordRepository repo = mock(PcmCostRecordRepository.class);
        PcmSourcingLane lane = new PcmSourcingLane();
        PcmCostRecord cr = new PcmCostRecord();
        when(repo.findCostRecordByExternalId(lane, "EXT")).thenReturn(Optional.of(cr));
        assertSame(cr, repo.findCostRecordByExternalId(lane, "EXT").orElseThrow());
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PcmSourcingLaneRepositoryTest {

    @Test
    void testStructure() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmSourcingLaneRepository.class));
        assertNotNull(PcmSourcingLaneRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testNullSiteConstantExposed() {
        assertNotNull(PcmSourcingLaneRepository.NULLSITE);
        assertEquals(-9999L, PcmSourcingLaneRepository.NULLSITE.getSiteKey());
    }

    @Test
    void testQueryMethodsAnnotated() throws Exception {
        String[] names = {
                "findSourcingLaneByNaturalKey",
                "findSLListByNaturalKey",
                "findFromSitesForSourcingLanes",
                "findToSitesForSourcingLanes",
                "findAllSourcingLanesForItem",
                "findSourcingLanes",
                "findSourcingLanesForItemSupplier",
                "findSourcingLanesForItem",
                "findSourcingLaneStatus",
                "findSourcingLaneStatusForOwner",
        };
        int annotated = 0;
        for (Method m : PcmSourcingLaneRepository.class.getDeclaredMethods()) {
            for (String n : names) {
                if (m.getName().equals(n) && !m.isDefault()) {
                    assertNotNull(m.getAnnotation(Query.class), "missing @Query on " + n);
                    annotated++;
                }
            }
        }
        assertTrue(annotated >= 10, "expected at least 10 @Query methods, found " + annotated);
    }

    @Test
    void testFindSLByNaturalKey_withoutBom_DelegatesWithNullBom() {
        PcmSourcingLaneRepository repo = mock(PcmSourcingLaneRepository.class);
        Item item = mock(Item.class);
        BusinessEntity be = mock(BusinessEntity.class);
        Site from = mock(Site.class);
        Site to = mock(Site.class);
        PcmSourcingLane lane = new PcmSourcingLane();

        when(repo.findSourcingLaneByNaturalKey(item, null, be, from, to, "USD")).thenReturn(lane);
        when(repo.findSLByNaturalKey(item, be, from, to, "USD")).thenCallRealMethod();

        assertSame(lane, repo.findSLByNaturalKey(item, be, from, to, "USD"));
        verify(repo).findSourcingLaneByNaturalKey(item, null, be, from, to, "USD");
    }

    @Test
    void testFindSLByNaturalKey_withBom_DelegatesToList() {
        PcmSourcingLaneRepository repo = mock(PcmSourcingLaneRepository.class);
        Item item = mock(Item.class);
        Bom bom = mock(Bom.class);
        BusinessEntity be = mock(BusinessEntity.class);
        Site from = mock(Site.class);
        Site to = mock(Site.class);
        List<PcmSourcingLane> out = Collections.singletonList(new PcmSourcingLane());

        when(repo.findSLListByNaturalKey(item, bom, be, from, to, "EUR")).thenReturn(out);
        when(repo.findSLByNaturalKey(item, bom, be, from, to, "EUR")).thenCallRealMethod();

        assertSame(out, repo.findSLByNaturalKey(item, bom, be, from, to, "EUR"));
        verify(repo).findSLListByNaturalKey(item, bom, be, from, to, "EUR");
    }

    @Test
    void testSaveOrUpdate_DelegatesToSave() {
        PcmSourcingLaneRepository repo = mock(PcmSourcingLaneRepository.class);
        PcmSourcingLane lane = new PcmSourcingLane();
        when(repo.save(lane)).thenReturn(lane);
        when(repo.saveOrUpdate(lane)).thenCallRealMethod();
        assertSame(lane, repo.saveOrUpdate(lane));
        verify(repo).save(lane);
    }
}

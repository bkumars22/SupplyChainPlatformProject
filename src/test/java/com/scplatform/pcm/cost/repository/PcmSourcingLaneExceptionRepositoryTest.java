/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.cost.entity.PcmSourcingLaneException;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PcmSourcingLaneExceptionRepositoryTest {

    @Test
    void testStructure() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmSourcingLaneExceptionRepository.class));
        assertNotNull(PcmSourcingLaneExceptionRepository.class.getAnnotation(Repository.class));
        assertNotNull(PcmSourcingLaneExceptionRepository.NULLSITE);
        assertEquals(-9999L, PcmSourcingLaneExceptionRepository.NULLSITE.getSiteKey());
    }

    @Test
    void testQueryAnnotation() throws Exception {
        Method m = PcmSourcingLaneExceptionRepository.class.getMethod(
                "findSLExceptionListByNaturalKey",
                Item.class, Bom.class, BusinessEntity.class,
                Site.class, Site.class, String.class);
        assertNotNull(m.getAnnotation(Query.class));
    }

    @Test
    void testDefault_withBom_Delegates() {
        PcmSourcingLaneExceptionRepository repo = mock(PcmSourcingLaneExceptionRepository.class);
        Item item = mock(Item.class);
        Bom bom = mock(Bom.class);
        BusinessEntity be = mock(BusinessEntity.class);
        Site from = mock(Site.class);
        Site to = mock(Site.class);
        List<PcmSourcingLaneException> out = Collections.emptyList();

        when(repo.findSLExceptionListByNaturalKey(item, bom, be, from, to, "USD")).thenReturn(out);
        when(repo.findSLExceptionByNaturalKey(item, bom, be, from, to, "USD")).thenCallRealMethod();

        assertSame(out, repo.findSLExceptionByNaturalKey(item, bom, be, from, to, "USD"));
        verify(repo).findSLExceptionListByNaturalKey(item, bom, be, from, to, "USD");
    }

    @Test
    void testDefault_withoutBom_PassesNull() {
        PcmSourcingLaneExceptionRepository repo = mock(PcmSourcingLaneExceptionRepository.class);
        Item item = mock(Item.class);
        BusinessEntity be = mock(BusinessEntity.class);
        Site from = mock(Site.class);
        Site to = mock(Site.class);
        List<PcmSourcingLaneException> out = Collections.emptyList();

        when(repo.findSLExceptionListByNaturalKey(item, null, be, from, to, "EUR")).thenReturn(out);
        when(repo.findSLExceptionByNaturalKey(item, be, from, to, "EUR")).thenCallRealMethod();

        assertSame(out, repo.findSLExceptionByNaturalKey(item, be, from, to, "EUR"));
        verify(repo).findSLExceptionListByNaturalKey(item, null, be, from, to, "EUR");
    }
}

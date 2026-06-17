/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import com.scplatform.pcm.cost.entity.PcmItemCategoryCostRecord;
import com.scplatform.pcm.cost.util.CostRollupUtil;
import com.scplatform.pcm.item.entity.ItemCategory;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PcmItemCategoryCostRecordRepositoryTest {

    @Test
    void testStructure() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmItemCategoryCostRecordRepository.class));
        assertNotNull(PcmItemCategoryCostRecordRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testQueryAnnotationsPresent() throws Exception {
        Method internal = PcmItemCategoryCostRecordRepository.class.getMethod(
                "findItemCategoryCostRecordByNaturalKeyInternal", ItemCategory.class);
        Method ctx = PcmItemCategoryCostRecordRepository.class.getMethod(
                "getFindItemCategoryCostRecordInContextCriteria", ItemCategory.class);
        assertNotNull(internal.getAnnotation(Query.class));
        assertNotNull(ctx.getAnnotation(Query.class));
    }

    @Test
    void testDefault_returnsNullForNoCommodity() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        when(repo.findItemCategoryCostRecordByNaturalKey(any())).thenCallRealMethod();
        assertNull(repo.findItemCategoryCostRecordByNaturalKey(CostRollupUtil.NOCOMMODITY));
        verify(repo, never()).findItemCategoryCostRecordByNaturalKeyInternal(any());
    }

    @Test
    void testDefault_returnsNullForCategoryEqualToNoCommodity() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        // Build a separate ItemCategory instance that .equals NOCOMMODITY (same key Long.MIN_VALUE)
        ItemCategory other = new ItemCategory(Long.MIN_VALUE);
        when(repo.findItemCategoryCostRecordByNaturalKey(any())).thenCallRealMethod();
        assertNull(repo.findItemCategoryCostRecordByNaturalKey(other));
    }

    private static ItemCategory nonNoCommodityCategory(String categoryId) {
        // ItemCategory.equals compares (categoryId, businessEntity); set categoryId to
        // ensure the instance does NOT .equals CostRollupUtil.NOCOMMODITY (which has both null).
        ItemCategory cat = new ItemCategory(42L);
        cat.setCategoryId(categoryId);
        return cat;
    }

    @Test
    void testDefault_delegatesForNormalCategory() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        ItemCategory cat = nonNoCommodityCategory("NORMAL");
        PcmItemCategoryCostRecord rec = new PcmItemCategoryCostRecord();
        when(repo.findItemCategoryCostRecordByNaturalKeyInternal(eq(cat)))
                .thenReturn(Optional.of(rec));
        when(repo.findItemCategoryCostRecordByNaturalKey(any())).thenCallRealMethod();

        assertSame(rec, repo.findItemCategoryCostRecordByNaturalKey(cat));
    }

    @Test
    void testDefault_returnsNullWhenInternalEmpty() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        ItemCategory cat = nonNoCommodityCategory("X");
        when(repo.findItemCategoryCostRecordByNaturalKeyInternal(eq(cat)))
                .thenReturn(Optional.empty());
        when(repo.findItemCategoryCostRecordByNaturalKey(any())).thenCallRealMethod();
        assertNull(repo.findItemCategoryCostRecordByNaturalKey(cat));
    }

    @Test
    void testFindInItemContext_returnsFirstResult() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        ItemCategory cat = nonNoCommodityCategory("NORMAL");
        PcmItemCategoryCostRecord rec = new PcmItemCategoryCostRecord();
        List<PcmItemCategoryCostRecord> list = Arrays.asList(rec);

        when(repo.getFindItemCategoryCostRecordInContextCriteria(cat)).thenReturn(list);
        when(repo.findItemCategoryCostRecordInItemContext(any())).thenCallRealMethod();

        assertSame(rec, repo.findItemCategoryCostRecordInItemContext(cat));
    }

    @Test
    void testFindInItemContext_returnsNullWhenEmpty() {
        PcmItemCategoryCostRecordRepository repo = mock(PcmItemCategoryCostRecordRepository.class);
        ItemCategory cat = nonNoCommodityCategory("X");
        when(repo.getFindItemCategoryCostRecordInContextCriteria(cat))
                .thenReturn(Collections.emptyList());
        when(repo.findItemCategoryCostRecordInItemContext(any())).thenCallRealMethod();
        assertNull(repo.findItemCategoryCostRecordInItemContext(cat));
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.supplyAllocation.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.supplyAllocation.entity.PcmSupplierAllocation;
import com.scplatform.pcm.supplyAllocation.repository.PcmSupplierAllocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PcmSupplierAllocationServiceTest {

    @Mock private PcmSupplierAllocationRepository repo;
    @Mock private PcmConfigUtil pcmConfigUtil;
    @InjectMocks private PcmSupplierAllocationService service;

    private final Item item = new Item();
    private final Site site = new Site();
    private final BusinessEntity be = new BusinessEntity();
    private final Date date = new Date();

    @Test
    void findSupplierAllocationForDate_delegatesToRepo() {
        PcmSupplierAllocation expected = new PcmSupplierAllocation();
        when(repo.findSupplierAllocationForDate(item, item, site, item, be, site, date)).thenReturn(expected);
        assertSame(expected, service.findSupplierAllocationForDate(item, item, site, item, be, site, date));
    }

    @Test
    void getSupplierAllocation_delegatesToRepo() {
        PcmSupplierAllocation a = new PcmSupplierAllocation();
        when(repo.getSupplierAllocation(5L)).thenReturn(a);
        assertSame(a, service.getSupplierAllocation(5L));
    }

    @Test
    void findSupplierAllocationByNaturalKey_delegates() {
        PcmSupplierAllocation a = new PcmSupplierAllocation();
        when(repo.findSupplierAllocationByNaturalKey(item, item, site, item, be, site, site, date, date)).thenReturn(a);
        assertSame(a, service.findSupplierAllocationByNaturalKey(item, item, site, item, be, site, site, date, date));
    }

    @Test
    void findSupplierAllocationBetweenDates_delegates() {
        List<PcmSupplierAllocation> list = Collections.singletonList(new PcmSupplierAllocation());
        when(repo.findSupplierAllocationBetweenDates(item, item, site, item, be, site, site, date, date, BigDecimal.ONE, true))
                .thenReturn(list);
        assertSame(list, service.findSupplierAllocationBetweenDates(item, item, site, item, be, site, site, date, date, BigDecimal.ONE, true));
    }

    @Test
    void findType2SupplierAllocationByItem_delegates() {
        PcmSupplierAllocation a = new PcmSupplierAllocation();
        when(repo.findType2SupplierAllocationByItem(item, site, item, date)).thenReturn(a);
        assertSame(a, service.findType2SupplierAllocationByItem(item, site, item, date));
    }

    @Test
    void findType2SupplierAllocationsByItem_singleDate_passesSameDateTwice() {
        List<PcmSupplierAllocation> list = Arrays.asList(new PcmSupplierAllocation(), new PcmSupplierAllocation());
        when(repo.findType2SupplierAllocationsByItem(item, site, date, date)).thenReturn(list);
        assertSame(list, service.findType2SupplierAllocationsByItem(item, site, date));
    }

    @Test
    void findType2SupplierAllocationsByItem_dateRange_delegates() {
        Date d2 = new Date();
        when(repo.findType2SupplierAllocationsByItem(item, site, date, d2)).thenReturn(Collections.emptyList());
        assertNotNull(service.findType2SupplierAllocationsByItem(item, site, date, d2));
    }

    @Test
    void findType2SupplierAllocationsByItemWithDestinationSite_passesFiscalFlag() {
        when(pcmConfigUtil.getBooleanValue(eq(PcmSupplierAllocationService.PCM_SUPPLIER_ALLOCATION_FISCAL_VALIDATION_SWITCH), eq(true)))
                .thenReturn(false);
        when(repo.findType2SupplierAllocationsByItemWithDestinationSite(item, site, site, date, date, false))
                .thenReturn(Collections.emptyList());
        List<PcmSupplierAllocation> result = service.findType2SupplierAllocationsByItemWithDestinationSite(item, site, site, date, date);
        assertNotNull(result);
        verify(repo).findType2SupplierAllocationsByItemWithDestinationSite(item, site, site, date, date, false);
    }

    @Test
    void getType2TotalSupplierAllocationsForItemPeriod_passesFiscalFlag_default() {
        when(pcmConfigUtil.getBooleanValue(any(), anyBoolean())).thenReturn(true);
        when(repo.getType2TotalSupplierAllocationsForItemPeriod(item, site, date, date, true))
                .thenReturn(BigDecimal.TEN);
        assertEquals(BigDecimal.TEN, service.getType2TotalSupplierAllocationsForItemPeriod(item, site, date, date));
    }

    @Test
    void findType2SupplierAllocationPeriodsForItem_delegates() {
        List<Date> dates = Collections.singletonList(date);
        when(repo.findType2SupplierAllocationPeriodsForItem(item)).thenReturn(dates);
        assertSame(dates, service.findType2SupplierAllocationPeriodsForItem(item));
    }

    @Test
    void delete_callsRepoDelete() {
        PcmSupplierAllocation a = new PcmSupplierAllocation();
        service.delete(a);
        verify(repo).delete(a);
    }

    @Test
    void deleteSupplierAllocationsByKey_emptyList_doesNothing() {
        service.deleteSupplierAllocationsByKey(Collections.emptyList());
        verify(repo, never()).deleteSupplierAllocationsByKey(any());
    }

    @Test
    void deleteSupplierAllocationsByKey_nullList_doesNothing() {
        service.deleteSupplierAllocationsByKey(null);
        verify(repo, never()).deleteSupplierAllocationsByKey(any());
    }

    @Test
    void deleteSupplierAllocationsByKey_delegates() {
        List<Long> keys = Arrays.asList(1L, 2L);
        when(repo.deleteSupplierAllocationsByKey(keys)).thenReturn(2);
        service.deleteSupplierAllocationsByKey(keys);
        verify(repo).deleteSupplierAllocationsByKey(keys);
    }

    @Test
    void deleteSupplierAllocationsByKey_repoThrows_propagates() {
        List<Long> keys = Collections.singletonList(1L);
        when(repo.deleteSupplierAllocationsByKey(keys)).thenThrow(new RuntimeException("db"));
        assertThrows(RuntimeException.class, () -> service.deleteSupplierAllocationsByKey(keys));
    }

    @Test
    void saveOrUpdate_delegates() {
        PcmSupplierAllocation a = new PcmSupplierAllocation();
        when(repo.saveOrUpdate(a)).thenReturn(a);
        assertSame(a, service.saveOrUpdate(a));
    }
}

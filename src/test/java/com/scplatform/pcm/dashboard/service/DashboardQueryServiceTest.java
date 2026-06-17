/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.dashboard.service;

import com.scplatform.pcm.bom.service.BomService;
import com.scplatform.pcm.cost.service.PcmCostRecordService;
import com.scplatform.pcm.cost.service.PcmSourcingLaneService;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.rebate.service.PcmRebateProgramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardQueryServiceTest {

    @Mock private ItemService itemService;
    @Mock private PcmSourcingLaneService pcmSourcingLaneService;
    @Mock private PcmCostRecordService pcmCostRecordService;
    @Mock private PcmRebateProgramService pcmRebateProgramService;
    @Mock private BomService bomService;
    @InjectMocks private DashboardQueryService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getNewUnassignedItems_delegatesToItemService() {
        List<Object[]> result = Collections.singletonList(new Object[]{"row"});
        when(itemService.getNewUnassignedItems(any(Date.class))).thenReturn(result);
        assertEquals(result, service.getNewUnassignedItems(7));
        ArgumentCaptor<Date> cap = ArgumentCaptor.forClass(Date.class);
        verify(itemService).getNewUnassignedItems(cap.capture());
        long expected = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
        assertTrue(Math.abs(cap.getValue().getTime() - expected) < 5_000);
    }

    @Test
    void getStatusCounts_costRecord_ownerOnlyTrue() {
        when(pcmCostRecordService.getCostRecordStatusForOwner(anyList(), any(), eq("u1")))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("costRecord", true, new String[] {"OPEN"}, 10, 1L, "u1");
        verify(pcmCostRecordService).getCostRecordStatusForOwner(anyList(), any(), eq("u1"));
        verify(pcmCostRecordService, never()).getCostRecordStatus(anyList(), any());
    }

    @Test
    void getStatusCounts_costRecord_ownerOnlyFalse() {
        when(pcmCostRecordService.getCostRecordStatus(anyList(), any()))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("costRecord", false, new String[] {"OPEN"}, 10, 1L, "u1");
        verify(pcmCostRecordService).getCostRecordStatus(anyList(), any());
        verify(pcmCostRecordService, never()).getCostRecordStatusForOwner(anyList(), any(), anyString());
    }

    @Test
    void getStatusCounts_sourcingLane_ownerOnlyTrue() {
        when(pcmSourcingLaneService.getSourcingLaneStatusForOwner(anyList(), any(), eq("u1")))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("sourcingLane", true, new String[] {"OPEN"}, 5, 7L, "u1");
        verify(pcmSourcingLaneService).getSourcingLaneStatusForOwner(anyList(), any(), eq("u1"));
    }

    @Test
    void getStatusCounts_sourcingLane_ownerOnlyFalse_passesUserKey() {
        when(pcmSourcingLaneService.getSourcingLaneStatus(anyList(), any(), eq(7L)))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("sourcingLane", false, new String[] {"OPEN"}, 5, 7L, "u1");
        verify(pcmSourcingLaneService).getSourcingLaneStatus(anyList(), any(), eq(7L));
    }

    @Test
    void getStatusCounts_forecast_alwaysCallsItemService() {
        when(itemService.getForecastStatus(anyList(), any())).thenReturn(Collections.emptyList());
        service.getStatusCounts("forecast", true, new String[] {"X"}, 5, 1L, "u");
        service.getStatusCounts("forecast", false, new String[] {"X"}, 5, 1L, "u");
        verify(itemService, org.mockito.Mockito.times(2)).getForecastStatus(anyList(), any());
    }

    @Test
    void getStatusCounts_forecastAdj() {
        when(itemService.getForecastAdjStatus(anyList(), any())).thenReturn(Collections.emptyList());
        service.getStatusCounts("forecast_ADJ", false, new String[] {"X"}, 5, 1L, "u");
        verify(itemService).getForecastAdjStatus(anyList(), any());
    }

    @Test
    void getStatusCounts_rebate_ownerOnly() {
        when(pcmRebateProgramService.getRebateProgramStatusForOwner(anyList(), any(), eq("u")))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("rebateProgram", true, new String[] {"X"}, 5, 1L, "u");
        verify(pcmRebateProgramService).getRebateProgramStatusForOwner(anyList(), any(), eq("u"));
    }

    @Test
    void getStatusCounts_rebate_nonOwner() {
        when(pcmRebateProgramService.getRebateProgramStatus(anyList(), any()))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("rebateProgram", false, new String[] {"X"}, 5, 1L, "u");
        verify(pcmRebateProgramService).getRebateProgramStatus(anyList(), any());
    }

    @Test
    void getStatusCounts_bom_ownerOnly() {
        when(bomService.getBomStatusForOwner(anyList(), any(), eq("u")))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("bom", true, new String[] {"X"}, 5, 1L, "u");
        verify(bomService).getBomStatusForOwner(anyList(), any(), eq("u"));
    }

    @Test
    void getStatusCounts_bom_nonOwner() {
        when(bomService.getBomStatus(anyList(), any())).thenReturn(Collections.emptyList());
        service.getStatusCounts("bom", false, new String[] {"X"}, 5, 1L, "u");
        verify(bomService).getBomStatus(anyList(), any());
    }

    @Test
    void getStatusCounts_unsupportedTypeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getStatusCounts("UNKNOWN", false, new String[]{"X"}, 5, 1L, "u"));
    }

    @Test
    void getStatusCounts_nullStatusesUsesNoneSentinel() {
        ArgumentCaptor<List<String>> cap = ArgumentCaptor.forClass(List.class);
        when(pcmCostRecordService.getCostRecordStatus(cap.capture(), any()))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("costRecord", false, null, 5, 1L, "u");
        assertEquals(Collections.singletonList("NONE"), cap.getValue());
    }

    @Test
    void getStatusCounts_emptyStatusesUsesNoneSentinel() {
        ArgumentCaptor<List<String>> cap = ArgumentCaptor.forClass(List.class);
        when(pcmCostRecordService.getCostRecordStatus(cap.capture(), any()))
                .thenReturn(Collections.emptyList());
        service.getStatusCounts("costRecord", false, new String[0], 5, 1L, "u");
        assertEquals(Collections.singletonList("NONE"), cap.getValue());
    }
}

/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.dashboard.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.bom.service.BomService;
import com.scplatform.pcm.cost.service.PcmCostRecordService;
import com.scplatform.pcm.cost.service.PcmSourcingLaneService;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.rebate.service.PcmRebateProgramService;
import com.scplatform.pcm.util.common.SCPlatformConstant;

@Service
public class DashboardQueryService {

    private final ItemService itemService;
    private final PcmSourcingLaneService pcmSourcingLaneService;
    private final PcmCostRecordService pcmCostRecordService;
    private final PcmRebateProgramService pcmRebateProgramService;
    private final BomService bomService;

    public DashboardQueryService(ItemService itemService,
                                 PcmSourcingLaneService pcmSourcingLaneService,
                                 PcmCostRecordService pcmCostRecordService,
                                 PcmRebateProgramService pcmRebateProgramService,
                                 BomService bomService) {
        this.itemService = itemService;
        this.pcmSourcingLaneService = pcmSourcingLaneService;
        this.pcmCostRecordService = pcmCostRecordService;
        this.pcmRebateProgramService = pcmRebateProgramService;
        this.bomService = bomService;
    }

    @Transactional(readOnly = true)
    public List<Object[]> getNewUnassignedItems(int daysOld) {
        return itemService.getNewUnassignedItems(cutoffDate(daysOld));
    }

    @Transactional(readOnly = true)
    public List<Object[]> getStatusCounts(String recordType, boolean ownerOnly,
                                          String[] statuses, int daysOld,
                                          Long userKey, String userId) {
        List<String> statusList = toStatusList(statuses);
        Date cutoff = cutoffDate(daysOld);

        switch (recordType) {
            case SCPlatformConstant.RECORD_TYPE_COST_RECORD:
                return ownerOnly
                        ? pcmCostRecordService.getCostRecordStatusForOwner(statusList, cutoff, userId)
                        : pcmCostRecordService.getCostRecordStatus(statusList, cutoff);

            case SCPlatformConstant.RECORD_TYPE_SOURCING_LANE:
                return ownerOnly
                        ? pcmSourcingLaneService.getSourcingLaneStatusForOwner(statusList, cutoff, userId)
                        : pcmSourcingLaneService.getSourcingLaneStatus(statusList, cutoff, userKey);

            case SCPlatformConstant.RECORD_TYPE_FORECAST:
                // Only a non-owner variant is defined for forecast.
                return itemService.getForecastStatus(statusList, cutoff);

            case SCPlatformConstant.RECORD_TYPE_FORECAST_ADJ:
                return itemService.getForecastAdjStatus(statusList, cutoff);

            case SCPlatformConstant.RECORD_TYPE_REBATE_PROGRAM:
                return ownerOnly
                        ? pcmRebateProgramService.getRebateProgramStatusForOwner(statusList, cutoff, userId)
                        : pcmRebateProgramService.getRebateProgramStatus(statusList, cutoff);

            case SCPlatformConstant.RECORD_TYPE_BOM:
                return ownerOnly
                        ? bomService.getBomStatusForOwner(statusList, cutoff, userId)
                        : bomService.getBomStatus(statusList, cutoff);

            default:
                throw new IllegalArgumentException("Unsupported dashboard recordType: " + recordType);
        }
    }

    // ==================== HELPERS ====================

    private static List<String> toStatusList(String[] statuses) {
        if (statuses == null || statuses.length == 0) {
            return Collections.singletonList("NONE");
        }
        return Arrays.asList(statuses);
    }

    private static Date cutoffDate(int daysOld) {
        return new Date(System.currentTimeMillis() - daysOld * 24L * 60L * 60L * 1000L);
    }
}


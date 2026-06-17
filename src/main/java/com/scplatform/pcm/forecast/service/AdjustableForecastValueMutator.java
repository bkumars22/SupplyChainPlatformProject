/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;


import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.common.enums.BigDecimalObjectType;
import com.scplatform.pcm.common.service.BigDecimalHelper;
import com.scplatform.pcm.forecast.dto.ForecastChange;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AdjustableForecastValueMutator {

    private final BigDecimalHelper bigDecimalHelper;
    private final PcmForecastValueService pcmForecastValueService;

    public void apply(PcmForecastValue pfv,
                      ForecastChange changeRecord,
                      ForecastTimeline timeline,
                      AdjustmentType type,
                      BigDecimal adjAmnt,
                      String amountText) {
        if (timeline == null || timeline.getPeriod(pfv.getEffectiveFromDt()) == null) {
            return;
        }
        PcmAdjustableForecastValue adjfv = (PcmAdjustableForecastValue) pfv;

        ChangeTracker<PcmForecastValue> pcmForecastValueChangeTracker = pcmForecastValueService.newChangeTracker(pfv);

        recordAdjustmentTypeChange(adjfv, changeRecord, type, amountText);
        recordAdjustmentAmountChange(adjfv, changeRecord, adjAmnt);

        adjfv.setAdjustmentAmount(adjAmnt);
        pcmForecastValueChangeTracker.firePropertyChangeEventUsingCompare("adjustmentAmount", adjfv.getAdjustmentAmount(), adjAmnt);
    }

    private void recordAdjustmentTypeChange(PcmAdjustableForecastValue adjfv,
                                            ForecastChange changeRecord,
                                            AdjustmentType type,
                                            String amountText) {
        ChangeTracker<PcmForecastValue> pcmForecastValueChangeTracker = pcmForecastValueService.newChangeTracker(adjfv);
        if (type != null && amountText != null) {
            String previous = adjfv.getAdjustmentType() != null
                    ? adjfv.getAdjustmentType().getString()
                    : null;
            changeRecord.record(
                    "ADJUSTMENT TYPE:" + pcmForecastValueService.getTitle(adjfv, false),
                    previous,
                    type.getString());
            adjfv.setAdjustmentType(type);
            pcmForecastValueChangeTracker.firePropertyChangeEvent("adjustmentType", adjfv.getAdjustmentType(), type);
        } else {
            adjfv.setAdjustmentType(null);
            pcmForecastValueChangeTracker.firePropertyChangeEvent("adjustmentType", adjfv.getAdjustmentType(), null);
        }
    }

    private void recordAdjustmentAmountChange(PcmAdjustableForecastValue adjfv,
                                              ForecastChange changeRecord,
                                              BigDecimal adjAmnt) {
        changeRecord.record(
                "ADJUSTMENT AMOUNT:" + pcmForecastValueService.getTitle(adjfv, false),
                bigDecimalHelper.normalize(BigDecimalObjectType.FORECASTS, adjfv.getAdjustmentAmount()),
                bigDecimalHelper.normalize(BigDecimalObjectType.FORECASTS, adjAmnt));
    }
}


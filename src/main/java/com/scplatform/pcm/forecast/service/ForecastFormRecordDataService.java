/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.service;


import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.scplatform.pcm.common.enums.Tense;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.AdjustableForecastValue;
import com.scplatform.pcm.forecast.dto.ForecastChange;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData.ForecastValue;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData.SimpleForecastValue;
import com.scplatform.pcm.forecast.dto.ForecastValues;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;
import com.scplatform.pcm.forecast.service.AdjustableForecastValueMutator;
import com.scplatform.pcm.util.validator.Errors;
import org.apache.commons.lang3.StringUtils;

@Service
public class ForecastFormRecordDataService
{
    private final PcmConfigUtil pcmConfigUtil;
    private final AdjustableForecastValueMutator adjustableForecastValueMutator;
    private final PcmForecastValueService pcmForecastValueService;

    public ForecastFormRecordDataService(PcmConfigUtil pcmConfigUtil,
                                         AdjustableForecastValueMutator adjustableForecastValueMutator,
                                         PcmForecastValueService pcmForecastValueService) {
        this.pcmConfigUtil = pcmConfigUtil;
        this.adjustableForecastValueMutator = adjustableForecastValueMutator;
        this.pcmForecastValueService = pcmForecastValueService;
    }

    public void copy(ForecastFormRecordData data, PcmForecast forecast) {
        if (forecast != null) {
            if (forecast.getSite() != null) {
                data.setSiteKey(forecast.getSite().getSiteKey());
            }
            if (forecast.getRemainingRollovers() != null) {
                data.setExtendPeriods(forecast.getRemainingRollovers().toString());
            }
            copyForecastValues(data, forecast);
        }
    }

    private void copyForecastValues(ForecastFormRecordData data, PcmForecast forecast) {
        for (PcmForecastValue pfv : forecast.getForecastValues()) {
            Tense forecastValueTense=Tense.getTenseForPeriod(pfv.getEffectiveFromDt(),
                    pfv.getEffectiveToDt());
            if (Tense.PAST != forecastValueTense) {
                ForecastValues values = data.getForecastValuesForDate(pfv
                        .getEffectiveFromDt());
                if (pfv instanceof PcmAdjustableForecastValue) {
                    PcmAdjustableForecastValue adjVal = (PcmAdjustableForecastValue) pfv;
                    AdjustableForecastValue value = createAdjustableForecastValueFromPcmForecastValue(data, adjVal);
                    values.setAdjustableMeasureValue(
                            pfv.getForecastMeasureKey(), value);
                    if (Tense.FUTURE == forecastValueTense && data.getRowAdjustmentType()==null && adjVal.getAdjustmentType()!=null) {
                        data.setRowAdjustmentType(adjVal.getAdjustmentType().name());
                    }
                } else if (pfv instanceof PcmSimpleForecastValue) {
                    PcmSimpleForecastValue simVal = (PcmSimpleForecastValue) pfv;
                    SimpleForecastValue value = createSimpleForecastValueFromPcmForecastValue(data, simVal);
                    values.setSimpleMeasureValue(pfv.getForecastMeasureKey(),
                            value);
                }
            }
        }
        if (data.getRowAdjustmentType() == null) {
            data.setRowAdjustmentType(pcmConfigUtil.getString("pcm.forecast.adjustment.type.default", AdjustmentType.FIXED.name()));
        }
        for (PcmForecastValue pfv : forecast.getForecastValues()) {
            Tense forecastValueTense=Tense.getTenseForPeriod(pfv.getEffectiveFromDt(),
                    pfv.getEffectiveToDt());
            if (Tense.FUTURE == forecastValueTense && pfv instanceof PcmAdjustableForecastValue) {
                ForecastValues values = data.getForecastValuesForDate(pfv
                        .getEffectiveFromDt());
                AdjustableForecastValue value = values.getAdjustableMeasureValue(pfv.getForecastMeasureKey());
                value.setAdjustmentType(data.getRowAdjustmentType());

            }
        }
    }

    private AdjustableForecastValue createAdjustableForecastValueFromPcmForecastValue(ForecastFormRecordData data, final PcmAdjustableForecastValue adjVal) {
        AdjustableForecastValue value = new AdjustableForecastValue();
        if (adjVal.getAdjustmentAmount() != null && adjVal.getAdjustmentType() != null) {
            value.setAdjustmentAmount(data.getNumberFormat().format(adjVal.getAdjustmentAmount()));
            value.setAdjustmentType(adjVal.getAdjustmentType().name());
        }
        return value;
    }

    private SimpleForecastValue createSimpleForecastValueFromPcmForecastValue(ForecastFormRecordData data, final PcmSimpleForecastValue pfv) {
        SimpleForecastValue sfv= new SimpleForecastValue();
        if (pfv.getForecastValue() != null) {
            sfv.setForecastValue(data.getNumberFormat().format(pfv.getForecastValue()));
        }
        return sfv;
    }

    // ---------------------------------------------------------------------
    // Behavior previously living on ForecastFormRecordData.SimpleForecastValue
    // and AdjustableForecastValue (validate / isValueSet / createPcmForecastValue
    // / updatePcmForecastValue). Moved here so the DTOs stay pure data holders.
    // ---------------------------------------------------------------------

    /** Validate a measure value submitted in the form. */
    public void validate(ForecastValue value, Errors errors, String prop) throws Exception {
        if (value instanceof SimpleForecastValue) {
            validateSimple((SimpleForecastValue) value, errors, prop);
        } else if (value instanceof AdjustableForecastValue) {
            validateAdjustable((AdjustableForecastValue) value, errors, prop);
        }
    }

    /** True when the user actually supplied a value worth persisting. */
    public boolean isValueSet(ForecastValue value) {
        if (value instanceof SimpleForecastValue) {
            return !StringUtils.isBlank(((SimpleForecastValue) value).getForecastValue());
        }
        // Adjustable values are always considered "set" (existing semantics).
        return value instanceof AdjustableForecastValue;
    }

    /** Factory: create the matching {@link PcmForecastValue} for the DTO type. */
    public PcmForecastValue createPcmForecastValue(ForecastValue value) {
        if (value instanceof SimpleForecastValue) {
            return new PcmSimpleForecastValue();
        }
        if (value instanceof AdjustableForecastValue) {
            return new PcmAdjustableForecastValue();
        }
        throw new IllegalArgumentException("Unknown ForecastValue type: " + value);
    }

    /** Apply the form value to the persistent forecast value, recording any change. */
    public void updatePcmForecastValue(ForecastValue value,
                                       PcmForecastValue pfv,
                                       ForecastChange changeRecord,
                                       ForecastTimeline timeline) {
        if (value instanceof SimpleForecastValue) {
            updateSimplePcmForecastValue((SimpleForecastValue) value, pfv, changeRecord);
        } else if (value instanceof AdjustableForecastValue) {
            AdjustableForecastValue adj = (AdjustableForecastValue) value;
            adjustableForecastValueMutator.apply(pfv, changeRecord, timeline,
                    adj.getType(), adj.getAdjAmnt(), adj.getAdjustmentAmount());
        }
    }

    // -- Simple ----------------------------------------------------------

    private void validateSimple(SimpleForecastValue sfv, Errors errors, String prop) {
        if (sfv.getForecastValue() == null) {
            return;
        }
        BigDecimal parsed;
        try {
            parsed = new BigDecimal(sfv.getForecastValue()).setScale(6, RoundingMode.HALF_UP);
        } catch (NumberFormatException nfe) {
            errors.addError("errors.field_decimal_required");
            throw nfe;
        }
        if (BigDecimal.ZERO.compareTo(parsed) > 0) {
            errors.addError("errors.field_positive_decimal_required");
            throw new IllegalArgumentException();
        }
        sfv.setValue(parsed);
    }

    private void updateSimplePcmForecastValue(SimpleForecastValue sfv,
                                              PcmForecastValue pfv,
                                              ForecastChange changeRecord) {
        PcmSimpleForecastValue simfv = (PcmSimpleForecastValue) pfv;
        ChangeTracker<PcmForecastValue> pcmForecastValueChangeTracker = pcmForecastValueService.newChangeTracker(pfv);
        changeRecord.record("VALUE:" + pcmForecastValueService.getTitle(simfv, false),
                simfv.getForecastValue(), sfv.getValue());
        simfv.setForecastValue(sfv.getValue());
        pcmForecastValueChangeTracker.firePropertyChangeEventUsingCompare("forecastValue", ((PcmSimpleForecastValue) pfv).getForecastValue(), sfv.getValue());
    }

    // -- Adjustable ------------------------------------------------------

    private void validateAdjustable(AdjustableForecastValue afv, Errors errors, String prop) throws Exception {
        if (StringUtils.isBlank(afv.getAdjustmentAmount())
                && StringUtils.isBlank(afv.getAdjustmentType())) {
            return;
        }
        validateAdjustmentAmount(afv, errors);
        validateAdjustmentType(afv, errors);
    }

    private void validateAdjustmentAmount(AdjustableForecastValue afv, Errors errors) {
        if (afv.getAdjustmentAmount() == null) {
            return;
        }
        try {
            afv.setAdjAmnt(new BigDecimal(afv.getAdjustmentAmount()).setScale(6, RoundingMode.HALF_UP));
        } catch (NumberFormatException e) {
            errors.addError("errors.field_decimal_required");
            throw e;
        }
    }

    private void validateAdjustmentType(AdjustableForecastValue afv, Errors errors) throws Exception {
        try {
            if (afv.getAdjustmentType() != null) {
                afv.setType(AdjustmentType.valueOf(afv.getAdjustmentType()));
            }
        } catch (Exception e) {
            errors.addError("errors.invalid", new Object[] { afv.getAdjustmentType() });
            throw e;
        }
    }
}


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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.common.enums.Tense;
import com.scplatform.pcm.forecast.dto.ForecastChange;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;
import com.scplatform.pcm.forecast.dto.ForecastPeriod;
import com.scplatform.pcm.forecast.dto.ForecastValues;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;
import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.util.validator.Errors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForecastFormService
{

    private final PcmForecastValueService pcmForecastValueService;
    private final ForecastFormRecordDataService forecastFormRecordDataService;

    public Errors validate(ForecastForm form, Errors request) {
        Errors results = (request != null) ? request : new Errors();
        boolean validationDetailsExist = false;

        for (Map.Entry<String, ForecastFormRecordData> formDataEntry : form.getForecastRecordData().entrySet()) {
            ForecastFormRecordData dataRecord = formDataEntry.getValue();
            PcmForecast forecast = form.getForecastRecord(formDataEntry.getKey());
            if (forecast == null) {
                continue;
            }

            ForecastChange changeRecord = form.getChangeRecord(forecast);
            if (dataRecord.getSiteKey() != null) {
                Site site = form.getSite(dataRecord.getSiteKey());
                changeRecord.record("SITE", forecast.getSite(), site);
                forecast.setSite(site);
            }

            // Test the forecast itself because we may not have sent anything in
            // but we have the value on the forecast
            if (forecast.getSite() == null) {
                validationDetailsExist = true;
                results.addError("errors.fc.site_required",
                        new Object[] { forecast.getForecastExternalId() });
            }

            // Period checks
            Integer extendPeriod = null;
            if (dataRecord.getExtendPeriods() != null) {
                try {
                    extendPeriod = NumberUtils.createInteger(dataRecord.getExtendPeriods());
                } catch (NumberFormatException nfe) {
                    validationDetailsExist = true;
                    results.addError("errors.fc.invalidRollover",
                            new Object[] { form.getMinRolloverPeriods(), form.getMaxRolloverPeriods() });
                }
            }
            changeRecord.record(PcmForecast.ROLLOVER_PERIOD_AUDIT, forecast.getRemainingRollovers(), extendPeriod);
            forecast.setRemainingRollovers(extendPeriod);

            if (forecast.getForecastModel() == ForecastModel.ADJUSTABLE) {
                // setting adjustment types for all future records where adjustment amount is not null
                AdjustmentType type = AdjustmentType.valueOf(dataRecord.getRowAdjustmentType());
                for (PcmForecastValue pfv : forecast.getForecastValues()) {
                    Tense forecastValueTense = Tense.getTenseForPeriod(pfv.getEffectiveFromDt(),
                            pfv.getEffectiveToDt());
                    ChangeTracker<PcmForecastValue> pcmForecastValueChangeTracker = pcmForecastValueService.newChangeTracker(pfv);
                    PcmAdjustableForecastValue adjfv = (PcmAdjustableForecastValue) pfv;
                    if (Tense.FUTURE == forecastValueTense) {
                        if (adjfv.getAdjustmentAmount() != null) {
                            if (type != null) {
                                changeRecord.record("ADJUSTMENT TYPE:" + pcmForecastValueService.getTitle(adjfv, false),
                                        adjfv.getAdjustmentType().getString(), type.getString());
                            } else {
                                changeRecord.record("ADJUSTMENT TYPE:" + pcmForecastValueService.getTitle(adjfv, false),
                                        adjfv.getAdjustmentType().getString(), type);
                            }
                            adjfv.setAdjustmentType(type);
                            pcmForecastValueChangeTracker.firePropertyChangeEvent("adjustmentType", adjfv.getAdjustmentType(), type);
                        } else {
                            adjfv.setAdjustmentType(null);
                            pcmForecastValueChangeTracker.firePropertyChangeEvent("adjustmentType", adjfv.getAdjustmentType(), null);
                        }
                    }
                }
            }

            // setting forecast values only for the buckets
            for (Entry<Date, ForecastValues> valueEntry : dataRecord.getForecastValuesMap().entrySet()) {
                Date periodStart = valueEntry.getKey();
                ForecastValues values = valueEntry.getValue();
                for (Object mn : values.getMeasureKeys()) {
                    String measureName = (String) mn;
                    ForecastFormRecordData.ForecastValue measureValue = values.getMeasureValue(measureName);
                    if (measureValue != null) {
                        try {
                            forecastFormRecordDataService.validate(measureValue, results,
                                    "forecastDataPeriodValue("
                                    + forecast.getForecastExternalId() + "."
                                    + periodStart.getTime() + "." + measureName + ")");
                        } catch (Exception nfe) {
                            validationDetailsExist = true;
                        }
                    }

                    PcmForecastValue pfv = forecast.getForecastValue(measureName, periodStart);

                    ChangeTracker<PcmForecastValue> pcmForecastValueChangeTracker = pcmForecastValueService.newChangeTracker(pfv);

                    ForecastTimeline timeLine = form.getCurrentTimeline();
                    if (ForecastModel.ADJUSTABLE.equals(forecast.getForecastModel())) {
                        timeLine = form.getAdjustableTimeline();
                    }
                    // Don't create an value if the value is null.
                    if (pfv == null && measureValue != null && forecastFormRecordDataService.isValueSet(measureValue)) {
                        ForecastPeriod period = timeLine.getPeriod(periodStart);
                        pfv = forecastFormRecordDataService.createPcmForecastValue(measureValue);

                        pfv.setForecastMeasureKey(measureName);
                        pcmForecastValueChangeTracker.firePropertyChangeEvent("measureKey", pfv.getForecastMeasureKey(), measureName);

                        pfv.setEffectiveFromDt(periodStart);
                        pcmForecastValueChangeTracker.firePropertyChangeEvent("effectiveFromDt", pfv.getEffectiveFromDt(), periodStart);

                        pfv.setEffectiveToDt(period.getEndDate());
                        pcmForecastValueChangeTracker.firePropertyChangeEvent("effectiveToDt", pfv.getEffectiveToDt(), period.getEndDate());

                        forecast.addForecastValue(pfv);
                    }
                    // Do we have something to store the data
                    if (pfv != null && measureValue != null) {
                        forecastFormRecordDataService.updatePcmForecastValue(measureValue, pfv, changeRecord, timeLine);
                    }
                }
            }
        }

        if (validationDetailsExist) {
            results.addError("errors.fc.validation_error");
        }
        return results;
    }

    public List<PcmForecast> getForecastRecordsBasedOnModel(ForecastForm form, String modelstr){
        ForecastModel model = ForecastModel.valueOf(modelstr);
        List<PcmForecast> forecastsBasedOnModel=new LinkedList<PcmForecast>();
        List<PcmForecast> forecasts=form.getForecastRecords();
        for(PcmForecast forecast:forecasts){
            if(forecast.getForecastModel() == model){
                forecastsBasedOnModel.add(forecast);
            }
        }
        return forecastsBasedOnModel;
    }

    /**
     * @param forecast
     * @return
     */
    public PcmForecast getCurrentForecastForAdjustableForecast(ForecastForm form, PcmForecast forecast) {
        if (forecast == null) {
            return null;
        }
        if (ForecastModel.CURRENT == forecast.getForecastModel()) {
            return forecast; // For current forecasts they themselves are their corresponding current forecasts
        } else {
            PcmForecast curForecast = null;
            Item fcItem = forecast.getItem();
            Site fcSite = forecast.getSite();
            if (fcItem == null || fcSite == null) {
                throw new IllegalStateException("Forecast item or site is null. Cannot find corresponding current forecast");
            }
            for (PcmForecast fc : form.getForecastRecordsCollection()) {
                if (ForecastModel.CURRENT == fc.getForecastModel()) {
                    if (fcItem.equals(fc.getItem()) && fcSite.equals(fc.getSite())) {
                        return fc;
                    }
                }
            }
            return null; // not found
        }
    }
}


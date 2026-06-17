/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.fiscalPeriod.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod.PeriodType;
import com.scplatform.pcm.fiscalPeriod.repository.FiscalPeriodRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FiscalPeriodService {

    /** Default number of future periods when no config override is present. */
    private static final int DEFAULT_NUM_FUTURE_PERIODS = 12;

    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final PcmConfigUtil          pcmConfigUtil;

    public List<FiscalPeriod> getFiscalPeriods(final Calendar startAt,
            final PeriodType fiscalPeriodType,
            int numberOfPastPeriods,
            int numberOfFuturePeriods) {
        List<FiscalPeriod> results = new ArrayList<>();

        if (numberOfPastPeriods > 0) {
            List<FiscalPeriod> pastPeriods = fiscalPeriodRepository
                    .findByFiscalPeriodTypeAndFiscalPeriodEndDateLessThanOrderByFiscalPeriodStartDateDesc(
                            fiscalPeriodType.getType(),
                            startAt.getTime(),
                            PageRequest.of(0, numberOfPastPeriods));

            for (FiscalPeriod period : pastPeriods) {
                results.add(0, period);
            }
        }

        List<FiscalPeriod> currentAndFuturePeriods = fiscalPeriodRepository
                .findByFiscalPeriodTypeAndFiscalPeriodEndDateGreaterThanEqualOrderByFiscalPeriodStartDateAsc(
                        fiscalPeriodType.getType(),
                        startAt.getTime(),
                        PageRequest.of(0, numberOfFuturePeriods + 1));

        results.addAll(currentAndFuturePeriods);
        return results;
    }

    public List<FiscalPeriod> getFutureFiscalMonths(String forecastType) {
        if (forecastType == null || forecastType.isBlank()) {
            return Collections.emptyList();
        }
        int numFuturePeriods = resolveNumFuturePeriods(forecastType);
        return getFiscalPeriods(Calendar.getInstance(), PeriodType.MONTH, 0, numFuturePeriods);
    }

    private int resolveNumFuturePeriods(String forecastType) {
        String key = "pcm.forecast." + forecastType + ".numFuturePeriods";
        return pcmConfigUtil.getIntValue(key, DEFAULT_NUM_FUTURE_PERIODS);
    }
}

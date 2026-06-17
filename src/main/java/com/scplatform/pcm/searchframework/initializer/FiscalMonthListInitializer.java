/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.initializer;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class FiscalMonthListInitializer implements SearchParameterInitializer {

    private static final String DEFAULT_FORECAST_TYPE = "COST";
    private static final String DISPLAY_DATE_FORMAT   = "MMM dd";
    private static final String SUBMIT_DATE_FORMAT    = "dd/MM/yyyy";

    private  FiscalPeriodService fiscalPeriodService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (!(parameter instanceof SearchParameterSelect)) {
            return false;
        }
        SearchParameterSelect list = (SearchParameterSelect) parameter;
        try {
            List<FiscalPeriod> periods =
                    fiscalPeriodService.getFutureFiscalMonths(DEFAULT_FORECAST_TYPE);

            SimpleDateFormat displayFmt = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
            SimpleDateFormat submitFmt  = new SimpleDateFormat(SUBMIT_DATE_FORMAT);
            int count = 1;

            for (FiscalPeriod fp : periods) {
                String value = submitFmt.format(fp.getFiscalPeriodStartDate())
                        + "~" + fp.getFiscalPeriodName().substring(0, 6)
                        + "~" + fp.getFiscalPeriod();
                list.addSelectValue(displayFmt.format(fp.getFiscalPeriodStartDate()), value);
                if (count == 1) {
                    list.setValue(value);
                }
                ++count;
            }
            return true;
        } catch (Exception e) {
            log.warn("Unable to initialize fiscal month list", e);
            return true;
        }
    }

    @Override
    public void setInitialData(String data) {
    }
}

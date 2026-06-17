/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.initializer;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.service.SiteService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class ForecastRegionListInitializer implements SearchParameterInitializer {

    private static final String DEFAULT_FORECAST_TYPE = "COST";

    private  SiteService siteService;

    @Override
    @SuppressWarnings("rawtypes")
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (!(parameter instanceof SearchParameterSelect)) {
            return false;
        }
        SearchParameterSelect list = (SearchParameterSelect) parameter;
        try {
            List<Site> sitesList = siteService.findForecastSites(DEFAULT_FORECAST_TYPE);

            int count = 1;
            for (Site site : sitesList) {
                String value = String.valueOf(site.getSiteKey());
                list.addSelectValue(site.getSiteDescription(), value);
                if (count == 1) {
                    list.setValue(value);
                }
                ++count;
            }
            return true;
        } catch (Exception e) {
            log.warn("Unable to initialize forecast region list", e);
            return true;
        }
    }

    @Override
    public void setInitialData(String data) {
    }
}

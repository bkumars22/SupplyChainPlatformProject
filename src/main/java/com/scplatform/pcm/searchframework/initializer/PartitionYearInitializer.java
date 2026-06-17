/**
 * @author sbhoi
 *
 * created on 22-09-2025
 *
 * Copyright (c) 2000-2018, by E2open LLC.
 * All rights reserved.
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.audit.Service.PcmAuditHistoryService;
import com.scplatform.pcm.common.cache.GlobalCache;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PartitionYearInitializer implements SearchParameterInitializer {

    private final GlobalCache globalCache;
    private final PcmAuditHistoryService pcmAuditHistoryService;

    private static final String AUDIT_YEAR_START = "AUDIT_START_YEAR";

    private static final String START_DATE_PREPEND = "%d-01-01";

    private static final String END_DATE_PREPEND = "%d-12-31";

    @Override
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (parameter instanceof SearchParameterSelect) {
            int startYear = 2000;
            if(!globalCache.containsKey(AUDIT_YEAR_START)){
                 Number minYear = pcmAuditHistoryService.getStartYear();
                 startYear = minYear.intValue();
                globalCache.put(AUDIT_YEAR_START, startYear);
            }else{
                startYear = (int) globalCache.get(AUDIT_YEAR_START);
            }
            SearchParameterSelect list = (SearchParameterSelect) parameter;
            for (int year = Year.now().getValue(); year >= startYear; year--) {
                String date = String.format(START_DATE_PREPEND, year)+"|"+String.format(END_DATE_PREPEND, year);
                list.addSelectValue(String.valueOf(year), date);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setInitialData(String data) {}
}

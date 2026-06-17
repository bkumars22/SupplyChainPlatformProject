/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;

public class FGCallableObj {

    private final FunctionalGroup functionalGroup;
    private final Map<FunctionalGroup, Map<PcmForecast, List<PcmSimpleForecastValue>>> affectedRecordsMap;
    private final List<Date> datesList;
    private final Map<PcmForecast, List<String>> errorsForecastMAP;
    private final List<String> allowedCRStatusList;
    private final List<Long> siteKeyList;
    private final boolean parentFGVariance;
    private final Map<FunctionalGroup, Map<Date, BigDecimal>> fgForecastValues;
    private final List<Date> fullTimelinedatesList;
    private final Map<Item, String> affectedItemErrorDesc;
    private final Map<FunctionalGroup, List<Long>> affectedSiteKeysMap;

    public FGCallableObj(FunctionalGroup functionalGroup,
                         Map<FunctionalGroup, Map<PcmForecast, List<PcmSimpleForecastValue>>> affectedRecordsMap,
                         List<Date> datesList,
                         Map<PcmForecast, List<String>> errorsForecastMAP,
                         List<String> allowedCRStatusList,
                         List<Long> siteKeyList,
                         boolean parentFGVariance,
                         Map<FunctionalGroup, Map<Date, BigDecimal>> fgForecastValues,
                         List<Date> fullTimelinedatesList,
                         Map<Item, String> affectedItemErrorDesc,
                         Map<FunctionalGroup, List<Long>> affectedSiteKeysMap) {
        this.functionalGroup       = functionalGroup;
        this.affectedRecordsMap    = affectedRecordsMap;
        this.datesList             = datesList;
        this.errorsForecastMAP     = errorsForecastMAP;
        this.allowedCRStatusList   = allowedCRStatusList;
        this.siteKeyList           = siteKeyList;
        this.parentFGVariance      = parentFGVariance;
        this.fgForecastValues      = fgForecastValues;
        this.fullTimelinedatesList = fullTimelinedatesList;
        this.affectedItemErrorDesc = affectedItemErrorDesc;
        this.affectedSiteKeysMap   = affectedSiteKeysMap;
    }

    public FunctionalGroup getFunctionalGroup()                     { return functionalGroup; }
    public Map<FunctionalGroup, Map<PcmForecast, List<PcmSimpleForecastValue>>>
                          getAffectedRecordsMap()                   { return affectedRecordsMap; }
    public List<Date>     getDatesList()                            { return datesList; }
    public Map<PcmForecast, List<String>> getErrorsForecastMAP()    { return errorsForecastMAP; }
    public List<String>   getAllowedCRStatusList()                  { return allowedCRStatusList; }
    public List<Long>     getSiteKeyList()                          { return siteKeyList; }
    public boolean        isParentFGVariance()                      { return parentFGVariance; }
    public Map<FunctionalGroup, Map<Date, BigDecimal>>
                          getFgForecastValues()                     { return fgForecastValues; }
    public List<Date>     getFullTimelinedatesList()                { return fullTimelinedatesList; }
    public Map<Item, String> getAffectedItemErrorDesc()             { return affectedItemErrorDesc; }
    public Map<FunctionalGroup, List<Long>> getAffectedSiteKeysMap(){ return affectedSiteKeysMap; }
}


/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FGCallableObjTest {

    @Test
    void constructor_exposesAllFields() {
        FunctionalGroup fg = new FunctionalGroup();
        Map<FunctionalGroup, Map<PcmForecast, List<PcmSimpleForecastValue>>> recs = new HashMap<>();
        List<Date> dates = Arrays.asList(new Date(1L), new Date(2L));
        Map<PcmForecast, List<String>> errs = new HashMap<>();
        List<String> statuses = Arrays.asList("OPEN");
        List<Long> siteKeys = Arrays.asList(100L);
        Map<FunctionalGroup, Map<Date, BigDecimal>> fgVals = new HashMap<>();
        List<Date> fullDates = Arrays.asList(new Date(3L));
        Map<Item, String> itemErr = new HashMap<>();
        Map<FunctionalGroup, List<Long>> affSites = new HashMap<>();

        FGCallableObj obj = new FGCallableObj(fg, recs, dates, errs, statuses, siteKeys,
                true, fgVals, fullDates, itemErr, affSites);

        assertSame(fg, obj.getFunctionalGroup());
        assertSame(recs, obj.getAffectedRecordsMap());
        assertSame(dates, obj.getDatesList());
        assertSame(errs, obj.getErrorsForecastMAP());
        assertSame(statuses, obj.getAllowedCRStatusList());
        assertSame(siteKeys, obj.getSiteKeyList());
        assertTrue(obj.isParentFGVariance());
        assertSame(fgVals, obj.getFgForecastValues());
        assertSame(fullDates, obj.getFullTimelinedatesList());
        assertSame(itemErr, obj.getAffectedItemErrorDesc());
        assertSame(affSites, obj.getAffectedSiteKeysMap());
    }

    @Test
    void constructor_acceptsFalseParentFGVariance() {
        FGCallableObj obj = new FGCallableObj(null, null, null, null, null,
                null, false, null, null, null, null);
        assertFalse(obj.isParentFGVariance());
        assertNull(obj.getFunctionalGroup());
        assertNull(obj.getAffectedRecordsMap());
    }
}

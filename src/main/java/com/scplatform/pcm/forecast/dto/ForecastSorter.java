/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.forecast.entity.PcmForecast;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class ForecastSorter implements Comparator<PcmForecast>
{
    @Override
    public int compare(PcmForecast f1, PcmForecast f2)
    {
        CompareToBuilder cb = new CompareToBuilder();
        cb.append(f1.getForecastType(),f2.getForecastType());

        // Group the groups together.... DANGER, DANGER Will Robinson... DELLism.
        StringBuilder group1 = new StringBuilder();
        StringBuilder group2 = new StringBuilder();
        cb.append(group1.toString(), group2.toString());

        cb.append(f1.getItem().getItemNumber(), f2.getItem().getItemNumber());
        // This causes new records to go at the bottom for a group within an item.
        cb.append(f1.getForecastKey() == null, f2.getForecastKey() == null);
        String site1 = (f1.getSite() != null) ? f1.getSite().getSiteDescription():null;
        String site2 = (f2.getSite() != null) ? f2.getSite().getSiteDescription():null;
        cb.append(site1,site2);
        cb.append(f1.getStatus(),f2.getStatus());
        return cb.toComparison();
    }
}
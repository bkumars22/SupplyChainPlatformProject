/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.forecast;

import java.util.List;
import java.util.Map;

import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.utilities.MessageIO;
import com.google.common.base.Preconditions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

public class ForecastCukeBuilder<T extends Forecast> {
    protected ForecastBuilder<T> builder;
    protected String forecastModel = null;
    protected List<Calendar> calendar;

    public ForecastBuilder<T> getBuilder() {
        return builder;
    }

    public String getForecastModel() {
        return this.forecastModel;
    }

    public List<Calendar> getCalendar() {
        return this.calendar;
    }

    public ForecastCukeBuilder(Class<T> messageClazz, DataTable parameters) {
        float min;
        float max;

//        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
//
//            Preconditions.checkArgument(row.containsKey("supplierAllocationKey"),
//                    "supplierAllocationKey parameter is not set");
//            MessageIO<SupplierAllocation> messageIOAllocation = new MessageIO<SupplierAllocation>(
//                    SupplierAllocation.class);
//            List<SupplierAllocation> supplierAllocation = messageIOAllocation.load(row.get("supplierAllocationKey"));
//
//            Preconditions.checkArgument(row.containsKey("calendarKey"), "calendarKey parameter is not set");
//            MessageIO<Calendar> messageIOCalendar = new MessageIO<Calendar>(Calendar.class);
//            calendar = messageIOCalendar.load(row.get("calendarKey"));
//
//            builder = new ForecastBuilder<T>(messageClazz, supplierAllocation, calendar);
//
//            if (row.containsKey("totalFiscalPeriods")) {
//                builder.withTotalFiscalPeriods(Integer.parseInt(row.get("totalFiscalPeriods")));
//            }
//
//            if (row.containsKey("offsetFiscalPeriods")) {
//                builder.withOffsetFiscalPeriods(Integer.parseInt(row.get("offsetFiscalPeriods")));
//            }
//
//            if ((row.containsKey("cost_min")) || (row.containsKey("cost_max"))) {
//                min = (row.containsKey("cost_min")) ? Float.parseFloat(row.get("cost_min")) : 1f;
//                max = (row.containsKey("cost_max")) ? Float.parseFloat(row.get("cost_min")) : 10f;
//                builder.withCostRange(min, max);
//            }
//
//            if ((row.containsKey("inventory_min")) || (row.containsKey("inventory_max"))) {
//                min = (row.containsKey("inventory_min")) ? Float.parseFloat(row.get("inventory_min")) : 5f;
//                max = (row.containsKey("inventory_max")) ? Float.parseFloat(row.get("inventory_max")) : 60f;
//                builder.withInventoryRange(min, max);
//            }
//
//            if (row.containsKey("forecastType")) {
//                builder.withForecastType(row.get("forecastType"));
//            }
//
//            if (row.containsKey("forecastModel")) {
//                forecastModel = row.get("forecastModel");
//                builder.withForecastModel(forecastModel);
//            }
//
//            if (row.containsKey("unitOfMeasure")) {
//                builder.withUnitOfMeasure(row.get("unitOfMeasure"));
//            }
//
//            if (row.containsKey("calendarName")) {
//                builder.withCalendarName(row.get("calendarName"));
//            }
//
//            if (row.containsKey("usePeriodNames")) {
//                boolean usePeriodNames = (row.get("usePeriodNames").toLowerCase().equals("true"));
//                builder.withPeriodNames(usePeriodNames);
//            }
//
//            break; // only doing 1 row
//        }
    }

}

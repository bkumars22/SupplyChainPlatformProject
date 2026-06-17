/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.businessEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.scplatform.cucumber.CukeHelper;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.google.common.base.Preconditions;

import io.cucumber.datatable.DataTable;


public class BusinessEntityCukeBuilder<T extends BusinessEntity> {
    protected BusinessEntityBuilder<T> builder;

    public BusinessEntityBuilder<T> getBuilder() {
        return builder;
    }

//    public BusinessEntityCukeBuilder(Class<T> messageClazz, DataTable parameters) {
//        DateTime dateTimeValue;
//        String stringValue;
//
//        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
//
//            Preconditions.checkArgument(row.containsKey("enterpriseCompany"), "enterpriseCompany parameter is not set");
//            stringValue = (String) Preprocessing.process(row.get("enterpriseCompany"));
//            Partner enterpriseCompany = (Partner) CukeHelper.findSavedClass(stringValue);
//            Preconditions.checkNotNull(enterpriseCompany, "enterpriseCompany parameter cannot be NULL!");
//
//            List<Partner> supplierCompanies = new ArrayList<Partner>();
//            if (row.containsKey("supplierCompanies")) {
//                stringValue = (String) Preprocessing.process(row.get("supplierCompanies"));
//                supplierCompanies = getPartners(stringValue);
//            }
//
//            List<Partner> manufacturerCompanies = new ArrayList<Partner>();
//            if (row.containsKey("manufacturerCompanies")) {
//                stringValue = (String) Preprocessing.process(row.get("manufacturerCompanies"));
//                manufacturerCompanies = getPartners(stringValue);
//            }
//
//            builder = new BusinessEntityBuilder<T>(messageClazz, enterpriseCompany, supplierCompanies,
//                    manufacturerCompanies);
//
//            if (row.containsKey("effectiveFromDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("effectiveFromDate"));
//                builder.withEffectiveFromDate(dateTimeValue);
//            }
//
//            if (row.containsKey("effectiveToDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("effectiveToDate"));
//                builder.withEffectiveToDate(dateTimeValue);
//            }
//
//            if (row.containsKey("site_effectiveFromDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("site_effectiveFromDate"));
//                builder.withSiteEffectiveFromDate(dateTimeValue);
//            }
//
//            if (row.containsKey("site_effectiveToDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("site_effectiveToDate"));
//                builder.withSiteEffectiveToDate(dateTimeValue);
//            }
//
//            if (row.containsKey("alternates_effectiveFromDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("alternates_effectiveFromDate"));
//                builder.withAlternatesEffectiveFromDate(dateTimeValue);
//            }
//
//            if (row.containsKey("alternates_effectiveToDate")) {
//                dateTimeValue = (DateTime) Preprocessing.process(row.get("alternates_effectiveToDate"));
//                builder.withAlternatesEffectiveToDate(dateTimeValue);
//            }
//
//            if (row.containsKey("operationCode")) {
//                stringValue = (String) Preprocessing.process(row.get("operationCode"));
//                builder.withOperationCode(stringValue);
//            }
//
//            if (row.containsKey("currencyCode")) {
//                stringValue = (String) Preprocessing.process(row.get("currencyCode"));
//                builder.withCurrencyCode(stringValue);
//            }
//
//            if (row.containsKey("site_DefaultCurrency")) {
//                stringValue = (String) Preprocessing.process(row.get("site_DefaultCurrency"));
//                builder.withSiteDefaultCurrency(stringValue);
//            }
//
//            break; // only doing 1 row
//        }
//
//    }

    private List<Partner> getPartners(String stringValue) {
        List<Partner> partners = new ArrayList<Partner>();

        String[] keyList = stringValue.split(";");
        for (String key : keyList) {
            Partner data = (Partner) CukeHelper.findSavedClass(key.trim());
            if (data == null) {
                JLog.error(this.getClass() + ".getPartners() - unable to find partner key: " + key);
            } else {
                partners.add(data);
            }
        }
        return partners;
    }
}

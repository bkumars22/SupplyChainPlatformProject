/*
 * @BusinessEntityBuilderFromDB.java@
 * Created on May 27, 2018
 *
 * Copyright (c) 2018 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
/**
 *
 */
package com.test.selenium.scplatform.messages.businessEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.utilities.Database;

/**
 * @author dgenrich
 *
 */
public class BusinessEntityBuilderFromDB {
    private String business_name;

    public BusinessEntityBuilderFromDB(String businessName) {
        business_name = businessName;
    }

    public List<BusinessEntity> build() {
        List<BusinessEntity> message = new ArrayList<>();
        BusinessEntity messageLine = BusinessEntity.Factory.newInstance();

        String[] columns = getBusinessEntityTableColumns().toArray(new String[getBusinessEntityTableColumns().size()]);
        HashMap<String, List<String>> businessEntityNameTable = Database.getResults(getBusinessEntityTableQuery(),
                columns);

        messageLine.setBusinessEntity(businessEntityNameTable.get("BUSINESS_ENTITY_IDENTIFIER").get(0));
        messageLine.setBusinessEntityName(businessEntityNameTable.get("BUSINESS_ENTITY_NAME").get(0));
        messageLine.setDescription(businessEntityNameTable.get("BUSINESS_ENTITY_DESC").get(0));
        messageLine.setBusinessEntityExternalId(businessEntityNameTable.get("BUSINESS_ENTITY_EXTERNAL_ID").get(0));
        messageLine.setBusinessEntityType(
                getBusinessEntityType(businessEntityNameTable.get("BUSINESS_ENTITY_TYPE_KEY").get(0)));
        messageLine.setContactName(getContactName(businessEntityNameTable.get("CONTACT_KEY").get(0)));
        messageLine.setContactUniqueId(getContactID(businessEntityNameTable.get("CONTACT_KEY").get(0)));
        messageLine.setDataSource(businessEntityNameTable.get("DATA_SOURCE").get(0));
        messageLine.setEffectiveFromDate(convertDate(businessEntityNameTable.get("EFFECTIVE_FROM_DT").get(0)));
        messageLine.setEffectiveToDate(convertDate(businessEntityNameTable.get("EFFECTIVE_TO_DT").get(0)));
        // messageLine.setOperationCode(operationCode);
        messageLine
                .setCurrency_CurrencyCode(getCurrencyCode(businessEntityNameTable.get("BUSINESS_ENTITY_KEY").get(0)));
        // messageLine.setCurrency_OperationCode(operationCode);

        String[] siteColumns = getSiteTableColumns().toArray(new String[getSiteTableColumns().size()]);
        HashMap<String, List<String>> siteTable = Database
                .getResults(getSiteTableQuery(businessEntityNameTable.get("BUSINESS_ENTITY_KEY").get(0)), siteColumns);

        // just get one site, random
        int index = RandomUtils.rand(0, siteTable.get("SITE_DESCRIPTION").size() - 1);

        messageLine.setSite_Site(siteTable.get("SITE_DESCRIPTION").get(index));
        messageLine.setSite_Description(siteTable.get("SITE_DESCRIPTION").get(index));
        messageLine.setSite_ParentSite(getParentSite(siteTable.get("PARENT_SITE_KEY").get(index)));
        messageLine.setSite_SiteType(siteTable.get("SITE_TYPE").get(index));
        messageLine.setSite_ContactName(getContactName(siteTable.get("CONTACT_KEY").get(index)));
        messageLine.setSite_ContactUniqueId(getContactID(siteTable.get("CONTACT_KEY").get(index)));
        messageLine.setSite_DefaultCurrency(siteTable.get("DEFAULT_CURRENCY_CODE").get(index));
        messageLine.setSite_EffectiveFromDate(convertDate(siteTable.get("EFFECTIVE_FROM_DT").get(index)));
        messageLine.setSite_EffectiveToDate(convertDate(siteTable.get("EFFECTIVE_TO_DT").get(index)));
        // messageLine.setSite_OperationCode(operationCode);

        if ("ENTERPRISE".equals(messageLine.getBusinessEntityType())) {
            String[] altColumns = getBusinessEntityAltTableColumns()
                    .toArray(new String[getBusinessEntityAltTableColumns().size()]);
            HashMap<String, List<String>> altTable = Database.getResults(
                    getSiteTableQuery(businessEntityNameTable.get("BUSINESS_ENTITY_KEY").get(0)), altColumns);

            // just get one alternative name, random
            index = RandomUtils.rand(0, altTable.get("BUSINESS_ENTITY_NAME").size() - 1);

            messageLine.setAlternates_AlternateName(altTable.get("BUSINESS_ENTITY_NAME").get(index));
            messageLine.setAlternates_EffectiveFromDate(convertDate(altTable.get("EFFECTIVE_FROM_DT").get(index)));
            messageLine.setAlternates_EffectiveToDate(convertDate(altTable.get("EFFECTIVE_TO_DT").get(index)));
            // messageLine.setAlternates_OperationCode(operationCode);
        }

        message.add(messageLine);
        return message;
    }

    protected String getBusinessEntityTableQuery() {
        return String.format("select %s from BUSINESS_ENTITY where BUSINESS_ENTITY_NAME=%s",
                String.join(",", getBusinessEntityTableColumns()), business_name);
    }

    protected List<String> getBusinessEntityTableColumns() {
        return Arrays.asList("BUSINESS_ENTITY_KEY", "BUSINESS_ENTITY_IDENTIFIER", "BUSINESS_ENTITY_NAME",
                "BUSINESS_ENTITY_DESC", "BUSINESS_ENTITY_EXTERNAL_ID", "BUSINESS_ENTITY_TYPE_KEY", "CONTACT_KEY",
                "DATA_SOURCE", "EFFECTIVE_FROM_DT", "EFFECTIVE_TO_DT");
    }

    protected String getSiteTableQuery(String BUSINESS_ENTITY_KEY) {
        return String.format("select %s SITE where BUSINESS_ENTITY_KEY=%s", String.join(",", getSiteTableColumns()),
                BUSINESS_ENTITY_KEY);
    }

    protected List<String> getSiteTableColumns() {
        return Arrays.asList("SITE_DESCRIPTION", "PARENT_SITE_KEY", "SITE_TYPE", "CONTACT_KEY", "DEFAULT_CURRENCY_CODE",
                "EFFECTIVE_FROM_DT", "EFFECTIVE_TO_DT");
    }

    protected String getBusinessEntityAltTableQuery(String BUSINESS_ENTITY_KEY) {
        return String.format("select %s from BUSINESS_ENTITY_ALT where BUSINESS_ENTITY_KEY=10",
                String.join(",", getBusinessEntityAltTableColumns()), BUSINESS_ENTITY_KEY);
    }

    protected List<String> getBusinessEntityAltTableColumns() {
        return Arrays.asList("BUSINESS_ENTITY_NAME", "EFFECTIVE_FROM_DT", "EFFECTIVE_TO_DT");
    }

    protected DateTime convertDate(String date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        return formatter.parseDateTime(date);
    }

    private String getBusinessEntityType(String BUSINESS_ENTITY_TYPE_KEY) {
        return Database.getResult(String.format(
                "select BUSINESS_ENTITY_TYPE_NAME from BUSINESS_ENTITY_TYPE where BUSINESS_ENTITY_TYPE_KEY=%s",
                BUSINESS_ENTITY_TYPE_KEY), "BUSINESS_ENTITY_TYPE_NAME");
    }

    private String getContactName(String CONTACT_KEY) {
        if (CONTACT_KEY == null) {
            return null;
        }
        return Database.getResult(String.format("select CONTACT_NAME from CONTACT where CONTACT_KEY=%s", CONTACT_KEY),
                "CONTACT_NAME");
    }

    private String getContactID(String CONTACT_KEY) {
        if (CONTACT_KEY == null) {
            return null;
        }
        return Database.getResult(String.format("select CONTACT_ID from CONTACT where CONTACT_KEY=%s", CONTACT_KEY),
                "CONTACT_ID");
    }

    private String getCurrencyCode(String BUSINESS_ENTITY_KEY) {
        return Database.getResult(
                String.format("select CURRENCY_CODE from BUSINESS_ENTITY_CURRENCY where BUSINESS_ENTITY_KEY=%s",
                        BUSINESS_ENTITY_KEY),
                "CURRENCY_CODE");
    }

    private String getParentSite(String PARENT_SITE_KEY) {
        if (PARENT_SITE_KEY == null) {
            return null;
        }
        return Database.getResult(String.format("select SITE_DESCRIPTION from SITE where SITE_KEY=%s", PARENT_SITE_KEY),
                "SITE_DESCRIPTION");
    }
}

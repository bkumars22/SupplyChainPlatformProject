/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.current;

import java.util.HashMap;
import java.util.Map;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ForecastDetailsCurrentModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Region")
    private String region;

    @DisplayName("Status")
    private String status;

    @DisplayName("Commodity")
    private String commodity;

    @DisplayName("Member of Group")
    private String memberOfGroup;

    @DisplayName("Responsibility")
    private String responsibility;

    @DisplayName("Rollover Expires On")
    private String rolloverExpiresOn;

    @DisplayName("Extend Forecast Term")
    private String extendForecastTerm;

    private Map<String, Float> pitBuckets;

    public void setPitBuckets(String dateLabel, Float pitValue) {
        if (pitBuckets == null) {
            pitBuckets = new HashMap<String, Float>();
        }
        pitBuckets.put(dateLabel, pitValue);
    }

    public float getPitBuckets(String dateLabel) {
        if (pitBuckets == null) {
            pitBuckets = new HashMap<String, Float>();
        }
        return pitBuckets.get(dateLabel);
    }

    public void setPitBuckets(Map<String, Float> buckets) {
        if (pitBuckets == null) {
            pitBuckets = new HashMap<String, Float>();
        }
        pitBuckets.putAll(buckets);
    }

    public boolean doesPitBucketExist(String dateLabel) {
        if (pitBuckets == null) {
            pitBuckets = new HashMap<String, Float>();
        }
        return pitBuckets.containsKey(dateLabel);
    }

    /**
     * @return the itemNumber
     */
    public String getItemNumber() {
        return itemNumber;
    }

    /**
     * @param itemNumber
     *            the itemNumber to set
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region
     *            the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the commodity
     */
    public String getCommodity() {
        return commodity;
    }

    /**
     * @param commodity
     *            the commodity to set
     */
    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    /**
     * @return the memberOfGroup
     */
    public String getMemberOfGroup() {
        return memberOfGroup;
    }

    /**
     * @param memberOfGroup
     *            the memberOfGroup to set
     */
    public void setMemberOfGroup(String memberOfGroup) {
        this.memberOfGroup = memberOfGroup;
    }

    /**
     * @return the responsibility
     */
    public String getResponsibility() {
        return responsibility;
    }

    /**
     * @param responsibility
     *            the responsibility to set
     */
    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    /**
     * @return the rolloverExpiresOn
     */
    public String getRolloverExpiresOn() {
        return rolloverExpiresOn;
    }

    /**
     * @param rolloverExpiresOn
     *            the rolloverExpiresOn to set
     */
    public void setRolloverExpiresOn(String rolloverExpiresOn) {
        this.rolloverExpiresOn = rolloverExpiresOn;
    }

    /**
     * @return the extendForecastTerm
     */
    public String getExtendForecastTerm() {
        return extendForecastTerm;
    }

    /**
     * @param extendForecastTerm
     *            the extendForecastTerm to set
     */
    public void setExtendForecastTerm(String extendForecastTerm) {
        this.extendForecastTerm = extendForecastTerm;
    }

    /**
     * @return the pitBuckets
     */
    public Map<String, Float> getPitBuckets() {
        return pitBuckets;
    }

}

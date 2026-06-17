/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Forecast -> Search Forecast
 */
public class SearchForecastResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Region")
    private String region;

    @DisplayName("Member of Group")
    private String memberOfGroup;

    @DisplayName("Commodity Name")
    private String commodityName;

    @DisplayName("Platform")
    private String platform;

    @DisplayName("Classification")
    private String classification;

    @DisplayName("Product Family")
    private String productFamily;

    @DisplayName("Forecast Model")
    private String forecastModel;

    @DisplayName("Status")
    private String status;

    @DisplayName("Extend Forecast Term")
    private String extendForecastTerm;

    @DisplayName("Last Changed On")
    private DateTime lastChangedOn;

    @DisplayName("Last Change By")
    private String lastChangeBy;

    @DisplayName("Responsibility")
    private String responsibility;

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

    public boolean pitBucketContains(String dateLabel) {
        boolean contains = false;
        if (pitBuckets == null) {
            contains = false;
        } else if (pitBuckets.containsKey(dateLabel)) {
            contains = true;
        }
        return contains;
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
     * @return the itemDescription
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * @param itemDescription
     *            the itemDescription to set
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
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
     * @return the commodityName
     */
    public String getCommodityName() {
        return commodityName;
    }

    /**
     * @param commodityName
     *            the commodityName to set
     */
    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform
     *            the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the classification
     */
    public String getClassification() {
        return classification;
    }

    /**
     * @param classification
     *            the classification to set
     */
    public void setClassification(String classification) {
        this.classification = classification;
    }

    /**
     * @return the productFamily
     */
    public String getProductFamily() {
        return productFamily;
    }

    /**
     * @param productFamily
     *            the productFamily to set
     */
    public void setProductFamily(String productFamily) {
        this.productFamily = productFamily;
    }

    /**
     * @return the forecastModel
     */
    public String getForecastModel() {
        return forecastModel;
    }

    /**
     * @param forecastModel
     *            the forecastModel to set
     */
    public void setForecastModel(String forecastModel) {
        this.forecastModel = forecastModel;
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
     * @return the lastChangedOn
     */
    public DateTime getLastChangedOn() {
        return lastChangedOn;
    }

    /**
     * @param lastChangedOn
     *            the lastChangedOn to set
     */
    public void setLastChangedOn(DateTime lastChangedOn) {
        this.lastChangedOn = lastChangedOn;
    }

    /**
     * @return the lastChangeBy
     */
    public String getLastChangeBy() {
        return lastChangeBy;
    }

    /**
     * @param lastChangeBy
     *            the lastChangeBy to set
     */
    public void setLastChangeBy(String lastChangeBy) {
        this.lastChangeBy = lastChangeBy;
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
     * @return the pitBuckets
     */
    public Map<String, Float> getPitBuckets() {
        return pitBuckets;
    }

}

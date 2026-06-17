/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Forecast -> Search Forecast
 */
public class SearchForecastModel extends Model {

    private static final long serialVersionUID = 1L;

    private String itemNumber;
    private String memberOfGroup;
    private String commodityName;
    private String multipleItemNumbers;
    private String status;
    private String multipleCommodityNames;
    private String assigned;
    private String responsibility;
    private String assignedTo;
    private String region;
    private String forecastModel;
    private String platform;
    private String lastChangedAfter;
    private String lastChangedBefore;
    private String lastChangeBy;
    private String extendForecastTerm;

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
     * @return the multipleItemNumbers
     */
    public String getMultipleItemNumbers() {
        return multipleItemNumbers;
    }

    /**
     * @param multipleItemNumbers
     *            the multipleItemNumbers to set
     */
    public void setMultipleItemNumbers(String multipleItemNumbers) {
        this.multipleItemNumbers = multipleItemNumbers;
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
     * @return the multipleCommodityNames
     */
    public String getMultipleCommodityNames() {
        return multipleCommodityNames;
    }

    /**
     * @param multipleCommodityNames
     *            the multipleCommodityNames to set
     */
    public void setMultipleCommodityNames(String multipleCommodityNames) {
        this.multipleCommodityNames = multipleCommodityNames;
    }

    /**
     * @return the assigned
     */
    public String getAssigned() {
        return assigned;
    }

    /**
     * @param assigned
     *            the assigned to set
     */
    public void setAssigned(String assigned) {
        this.assigned = assigned;
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
     * @return the assignedTo
     */
    public String getAssignedTo() {
        return assignedTo;
    }

    /**
     * @param assignedTo
     *            the assignedTo to set
     */
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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
     * @return the lastChangedAfter
     */
    public String getLastChangedAfter() {
        return lastChangedAfter;
    }

    /**
     * @param lastChangedAfter
     *            the lastChangedAfter to set
     */
    public void setLastChangedAfter(String lastChangedAfter) {
        this.lastChangedAfter = lastChangedAfter;
    }

    /**
     * @return the lastChangedBefore
     */
    public String getLastChangedBefore() {
        return lastChangedBefore;
    }

    /**
     * @param lastChangedBefore
     *            the lastChangedBefore to set
     */
    public void setLastChangedBefore(String lastChangedBefore) {
        this.lastChangedBefore = lastChangedBefore;
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

}

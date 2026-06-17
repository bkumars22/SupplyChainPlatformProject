/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords.sourcingLaneInformation;

import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.model.Model;

public class SourcingLaneInformationModel extends Model {

    private static final long serialVersionUID = 1L;

    private String laneName;
    @AutoPopulateOff
    private String item;
    private String supplier;
    private String currency;
    private String endDatesAreRequiredForPricing;
    private String collaboration;
    @AutoPopulateOff
    private String status;
    private String destinationSite;
    private String sourceSite;
    private String offsetInDays;
    private String productState;

    /**
     * @return the laneName
     */
    public String getLaneName() {
        return laneName;
    }

    /**
     * @param laneName
     *            the laneName to set
     */
    public void setLaneName(String laneName) {
        this.laneName = laneName;
    }

    /**
     * @return the item
     */
    public String getItem() {
        return item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(String item) {
        this.item = item;
    }

    /**
     * @return the supplier
     */
    public String getSupplier() {
        return supplier;
    }

    /**
     * @param supplier
     *            the supplier to set
     */
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /**
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @param currency
     *            the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the endDatesAreRequiredForPricing
     */
    public String getEndDatesAreRequiredForPricing() {
        return endDatesAreRequiredForPricing;
    }

    /**
     * @param endDatesAreRequiredForPricing
     *            the endDatesAreRequiredForPricing to set
     */
    public void setEndDatesAreRequiredForPricing(String endDatesAreRequiredForPricing) {
        this.endDatesAreRequiredForPricing = endDatesAreRequiredForPricing;
    }

    /**
     * @return the collaboration
     */
    public String getCollaboration() {
        return collaboration;
    }

    /**
     * @param collaboration
     *            the collaboration to set
     */
    public void setCollaboration(String collaboration) {
        this.collaboration = collaboration;
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
     * @return the destinationSite
     */
    public String getDestinationSite() {
        return destinationSite;
    }

    /**
     * @param destinationSite
     *            the destinationSite to set
     */
    public void setDestinationSite(String destinationSite) {
        this.destinationSite = destinationSite;
    }

    /**
     * @return the sourceSite
     */
    public String getSourceSite() {
        return sourceSite;
    }

    /**
     * @param sourceSite
     *            the sourceSite to set
     */
    public void setSourceSite(String sourceSite) {
        this.sourceSite = sourceSite;
    }

    /**
     * @return the offsetInDays
     */
    public String getOffsetInDays() {
        return offsetInDays;
    }

    /**
     * @param offsetInDays
     *            the offsetInDays to set
     */
    public void setOffsetInDays(String offsetInDays) {
        this.offsetInDays = offsetInDays;
    }

    /**
     * @return the productState
     */
    public String getProductState() {
        return productState;
    }

    /**
     * @param productState
     *            the productState to set
     */
    public void setProductState(String productState) {
        this.productState = productState;
    }

}

/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane.details;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class SourcingLaneInformationModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Existing Lanes")
    private String existingLanes;

    @DisplayName("Lane Name")
    private String laneName;

    @AutoPopulateOff
    @DisplayName("Item")
    private String item;

    @DisplayName("Manufacturer")
    private String supplier;

    @DisplayName("Currency")
    private String currency;

    @DisplayName("End dates are required for pricing")
    private String endDatesAreRequiredForPricing;

    @DisplayName("Collaboration")
    private String collaboration;

    @AutoPopulateOff
    @DisplayName("Status")
    private String status;

    @DisplayName("Destination Site")
    private String destinationSite;

    @DisplayName("Source Supplier")
    private String sourceSupplier;

    @DisplayName("Offset in Days")
    private String offsetInDays;

    @DisplayName("Product State")
    private String productState;

    public void print() {
        write("laneName", laneName);
        write("item", item);
        write("supplier", supplier);
        write("currency", currency);
        write("status", status);
        write("destinationSite", destinationSite);
        write("sourceSupplier", sourceSupplier);
        write("offsetInDays", offsetInDays);
        write("productState", productState);
        write("endDatesAreRequiredForPricing", endDatesAreRequiredForPricing);
        write("collaboration", collaboration);
    }

    private void write(String variableName, String variableValue) {
        JLog.write(String.format("%s = '%s'", getDisplayName(variableName), variableValue));
    }

    /**
     * @return the existingLanes
     */
    public String getExistingLanes() {
        return existingLanes;
    }

    /**
     * @param existingLanes
     *            the existingLanes to set
     */
    public void setExistingLanes(String existingLanes) {
        this.existingLanes = existingLanes;
    }

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
     * @return the sourceSupplier
     */
    public String getSourceSupplier() {
        return sourceSupplier;
    }

    /**
     * @param sourceSupplier
     *            the sourceSupplier to set
     */
    public void setSourceSupplier(String sourceSupplier) {
        this.sourceSupplier = sourceSupplier;
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

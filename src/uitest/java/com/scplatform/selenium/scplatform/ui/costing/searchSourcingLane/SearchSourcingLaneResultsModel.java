/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchSourcingLane;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Costing -> Search Sourcing Lane
 *
 */
public class SearchSourcingLaneResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Status")
    private String status;

    @DisplayName("Commodity Name")
    private String commodityName;

    @DisplayName("Item")
    private String item;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Item Business")
    private String itemBusiness;

    @DisplayName("Supplier")
    private String supplier;

    @DisplayName("Source Site")
    private String sourceSite;

    @DisplayName("Destination Site")
    private String destinationSite;

    @DisplayName("Currency")
    private String currency;

    @DisplayName("Product State")
    private String productState;

    @DisplayName("Responsibility")
    private String responsibility;

    @DisplayName("Non-Managed Cost Adjustment")
    private String nonManagedCostAdjustment;

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
     * @return the itemBusiness
     */
    public String getItemBusiness() {
        return itemBusiness;
    }

    /**
     * @param itemBusiness
     *            the itemBusiness to set
     */
    public void setItemBusiness(String itemBusiness) {
        this.itemBusiness = itemBusiness;
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
     * @return the nonManagedCostAdjustment
     */
    public String getNonManagedCostAdjustment() {
        return nonManagedCostAdjustment;
    }

    /**
     * @param nonManagedCostAdjustment
     *            the nonManagedCostAdjustment to set
     */
    public void setNonManagedCostAdjustment(String nonManagedCostAdjustment) {
        this.nonManagedCostAdjustment = nonManagedCostAdjustment;
    }

}

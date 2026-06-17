/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.dialogs.itemDetails;

import org.joda.time.DateTime;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ItemDetailsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Id")
    private String itemId;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Item Type")
    private String itemType;

    @DisplayName("Managed By")
    private String managedBy;

    @DisplayName("Revision")
    private String revision;

    @DisplayName("Version")
    private String version;

    @DisplayName("Top Level Item")
    private String topLevelItem;

    @DisplayName("Cost Commodity")
    private String costCommodity;

    @DisplayName("Classification")
    private String classification;

    @DisplayName("Platform")
    private String platform;

    @DisplayName("Product Family")
    private String productFamily;

    @DisplayName("State")
    private String state;

    @DisplayName("UOM")
    private String uom;

    @DisplayName("Inventory")
    private String inventory;

    @DisplayName("Item Business")
    private String itemBusiness;

    @DisplayName("First Loaded On")
    private DateTime firstLoadedOn;

    @DisplayName("Last Updated On")
    private DateTime lastUpdatedOn;

    @DisplayName("Source System")
    private String sourceSystem;

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId
     *            the itemId to set
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType
     *            the itemType to set
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the managedBy
     */
    public String getManagedBy() {
        return managedBy;
    }

    /**
     * @param managedBy
     *            the managedBy to set
     */
    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision
     *            the revision to set
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the topLevelItem
     */
    public String getTopLevelItem() {
        return topLevelItem;
    }

    /**
     * @param topLevelItem
     *            the topLevelItem to set
     */
    public void setTopLevelItem(String topLevelItem) {
        this.topLevelItem = topLevelItem;
    }

    /**
     * @return the costCommodity
     */
    public String getCostCommodity() {
        return costCommodity;
    }

    /**
     * @param costCommodity
     *            the costCommodity to set
     */
    public void setCostCommodity(String costCommodity) {
        this.costCommodity = costCommodity;
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
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the uom
     */
    public String getUom() {
        return uom;
    }

    /**
     * @param uom
     *            the uom to set
     */
    public void setUom(String uom) {
        this.uom = uom;
    }

    /**
     * @return the inventory
     */
    public String getInventory() {
        return inventory;
    }

    /**
     * @param inventory
     *            the inventory to set
     */
    public void setInventory(String inventory) {
        this.inventory = inventory;
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
     * @return the firstLoadedOn
     */
    public DateTime getFirstLoadedOn() {
        return firstLoadedOn;
    }

    /**
     * @param firstLoadedOn
     *            the firstLoadedOn to set
     */
    public void setFirstLoadedOn(DateTime firstLoadedOn) {
        this.firstLoadedOn = firstLoadedOn;
    }

    /**
     * @return the lastUpdatedOn
     */
    public DateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    /**
     * @param lastUpdatedOn
     *            the lastUpdatedOn to set
     */
    public void setLastUpdatedOn(DateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    /**
     * @return the sourceSystem
     */
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @param sourceSystem
     *            the sourceSystem to set
     */
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

}

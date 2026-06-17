/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.items;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class SearchItemsResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Item Type")
    private String itemType;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Item Business")
    private String itemBusiness;

    @DisplayName("Revision")
    private String revision;

    @DisplayName("Commodity Name")
    private String commodityName;

    @DisplayName("Member of Group")
    private String memberOfGroup;

    @DisplayName("Platform")
    private String platform;

    @DisplayName("Classification")
    private String classification;

    @DisplayName("Product Family")
    private String productFamily;

    @DisplayName("Responsibility")
    private String responsibility;

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

}

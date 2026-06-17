/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class ItemAssignmentResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Item Type")
    private String itemType;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Cost Commodity (Managed By)")
    private String costCommodity;

    @DisplayName("Business Name")
    private String businessName;

    @DisplayName("Responsibility")
    private String responsibility;

    @DisplayName("Days Since Added")
    private String daysSinceAdded;

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
     * @return the businessName
     */
    public String getBusinessName() {
        return businessName;
    }

    /**
     * @param businessName
     *            the businessName to set
     */
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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
     * @return the daysSinceAdded
     */
    public String getDaysSinceAdded() {
        return daysSinceAdded;
    }

    /**
     * @param daysSinceAdded
     *            the daysSinceAdded to set
     */
    public void setDaysSinceAdded(String daysSinceAdded) {
        this.daysSinceAdded = daysSinceAdded;
    }

}

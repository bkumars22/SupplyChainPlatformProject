/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment;

import com.test.selenium.common.modelViewController.model.Model;

public class ItemAssignmentModel extends Model {

    private static final long serialVersionUID = 1L;

    private String itemNumber;
    private String itemBusiness;
    private String costCommodity;
    private String multipleItemNumbers;
    private String itemType;
    private String managedBy;
    private String daysSinceAdded;
    private String assigned;
    private String responsibility;
    private String assignedTo;
    private String platform;

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

}

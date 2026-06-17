/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details;

import org.joda.time.DateTime;

import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class SupplierAllocationModel extends Model {

    private static final long serialVersionUID = 1L;

    @AutoPopulateOff
    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Period Type")
    private String periodType;

    @AutoPopulateOff
    @DisplayName("Item Description")
    private String itemDescription;

    @AutoPopulateOff
    @DisplayName("Managed By")
    private String managedBy;

    @DisplayName("Period Start")
    private DateTime periodStart;

    @DisplayName("Period End")
    private DateTime periodEnd;

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
     * @return the periodType
     */
    public String getPeriodType() {
        return periodType;
    }

    /**
     * @param periodType
     *            the periodType to set
     */
    public void setPeriodType(String periodType) {
        this.periodType = periodType;
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
     * @return the periodStart
     */
    public DateTime getPeriodStart() {
        return periodStart;
    }

    /**
     * @param periodStart
     *            the periodStart to set
     */
    public void setPeriodStart(DateTime periodStart) {
        this.periodStart = periodStart;
    }

    /**
     * @return the periodEnd
     */
    public DateTime getPeriodEnd() {
        return periodEnd;
    }

    /**
     * @param periodEnd
     *            the periodEnd to set
     */
    public void setPeriodEnd(DateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

}

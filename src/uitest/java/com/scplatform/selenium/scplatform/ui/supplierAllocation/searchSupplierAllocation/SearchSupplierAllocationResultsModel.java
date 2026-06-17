/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import org.joda.time.DateTime;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Supply Allocation -> Search Supply Allocation <br>
 * Fields for the search results.
 *
 */
public class SearchSupplierAllocationResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Supplier Item Number")
    private String supplierItemNumber;

    @DisplayName("Supplier")
    private String supplier;

    @DisplayName("Supplier Site")
    private String supplierSite;

    @DisplayName("Allocation")
    private float allocation;

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
     * @return the supplierItemNumber
     */
    public String getSupplierItemNumber() {
        return supplierItemNumber;
    }

    /**
     * @param supplierItemNumber
     *            the supplierItemNumber to set
     */
    public void setSupplierItemNumber(String supplierItemNumber) {
        this.supplierItemNumber = supplierItemNumber;
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
     * @return the supplierSite
     */
    public String getSupplierSite() {
        return supplierSite;
    }

    /**
     * @param supplierSite
     *            the supplierSite to set
     */
    public void setSupplierSite(String supplierSite) {
        this.supplierSite = supplierSite;
    }

    /**
     * @return the allocation
     */
    public float getAllocation() {
        return allocation;
    }

    /**
     * @param allocation
     *            the allocation to set
     */
    public void setAllocation(float allocation) {
        this.allocation = allocation;
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

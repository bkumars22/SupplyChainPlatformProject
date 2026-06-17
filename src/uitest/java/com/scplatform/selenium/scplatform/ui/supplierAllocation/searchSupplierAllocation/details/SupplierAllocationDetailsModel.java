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

public class SupplierAllocationDetailsModel extends Model {

    private static final long serialVersionUID = 1L;

    @AutoPopulateOff
    @DisplayName("Supplier Item Number")
    private String supplierItemNumber;

    @AutoPopulateOff
    @DisplayName("Supplier")
    private String supplier;

    @AutoPopulateOff
    @DisplayName("Supplier Site")
    private String supplierSite;

    @DisplayName("Allocation")
    private float allocation;

    @AutoPopulateOff
    @DisplayName("Price")
    private String price;

    @DisplayName("Description")
    private String description;

    @AutoPopulateOff
    @DisplayName("Period Start")
    private DateTime periodStart;

    @AutoPopulateOff
    @DisplayName("Period End")
    private DateTime periodEnd;

    @AutoPopulateOff
    @DisplayName("Status")
    private String status;

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
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price
     *            the price to set
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

}

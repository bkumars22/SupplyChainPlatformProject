/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * Navigation: Supply Allocation -> Search Supply Allocation <br>
 * Fields for the search critera.
 */
public class SearchSupplierAllocationModel extends Model {

    private static final long serialVersionUID = 1L;

    private String itemNumber;
    private String periodStart;
    private String periodEnd;
    private String multipleItemNumbers;
    private String supplier;

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
     * @return the periodStart
     */
    public String getPeriodStart() {
        return periodStart;
    }

    /**
     * @param periodStart
     *            the periodStart to set
     */
    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    /**
     * @return the periodEnd
     */
    public String getPeriodEnd() {
        return periodEnd;
    }

    /**
     * @param periodEnd
     *            the periodEnd to set
     */
    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
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

}

/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class BomManagementResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Item Description")
    private String itemDescription;

    @DisplayName("Business Name")
    private String businessName;

    @DisplayName("Status")
    private String status;

    @DisplayName("Version")
    private String version;

    @DisplayName("Revision")
    private String revision;

    @DisplayName("BOM Description")
    private String bomDescription;

    @DisplayName("Top Level Item")
    private String topLevelItem;

    @DisplayName("Repairs")
    private String repairs;

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
     * @return the bomDescription
     */
    public String getBomDescription() {
        return bomDescription;
    }

    /**
     * @param bomDescription
     *            the bomDescription to set
     */
    public void setBomDescription(String bomDescription) {
        this.bomDescription = bomDescription;
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
     * @return the repairs
     */
    public String getRepairs() {
        return repairs;
    }

    /**
     * @param repairs
     *            the repairs to set
     */
    public void setRepairs(String repairs) {
        this.repairs = repairs;
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

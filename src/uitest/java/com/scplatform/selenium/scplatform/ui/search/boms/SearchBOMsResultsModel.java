/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.boms;

import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class SearchBOMsResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @DisplayName("Item Number")
    private String itemNumber;

    @DisplayName("Business Name")
    private String businessName;

    @DisplayName("Platform")
    private String platform;

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

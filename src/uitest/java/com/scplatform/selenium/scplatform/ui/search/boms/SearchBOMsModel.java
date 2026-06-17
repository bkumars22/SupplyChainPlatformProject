/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.boms;

import com.test.selenium.common.modelViewController.model.Model;

public class SearchBOMsModel extends Model {

    private static final long serialVersionUID = 1L;

    private String itemNumber;
    private String itemBusiness;
    private String platform;
    private String multipleItemNumbers;
    private String status;
    private String revision;
    private String topLevelItem;
    private String usedAsSubassembly;
    private String version;
    private String assigned;
    private String responsibility;
    private String assignedTo;
    private String bomDescription;
    private String repairs;

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
     * @return the usedAsSubassembly
     */
    public String getUsedAsSubassembly() {
        return usedAsSubassembly;
    }

    /**
     * @param usedAsSubassembly
     *            the usedAsSubassembly to set
     */
    public void setUsedAsSubassembly(String usedAsSubassembly) {
        this.usedAsSubassembly = usedAsSubassembly;
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

}

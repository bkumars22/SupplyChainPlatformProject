/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import com.test.selenium.common.modelViewController.model.Model;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

public class BomManagementModel extends Model {

    private static final long serialVersionUID = 1L;

    private String itemNumber;
    private String revision;
    private String topLevelItem;
    private String multipleItemNumbers;
    private String status;
    private String replacePending;
    private String assigned;
    private String responsibility;
    private String assignedTo;
    private String platform;
    private String usedAsSubassembly;
    private String repairs;

    /**
     * @return the itemNumber
     */
    public String getItemNumber() {
        return itemNumber;
    }

    /**
     * @param itemNumber
     *                   the itemNumber to set
     */
    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    /**
     * @return the revision
     */
    public String getRevision() {
        return revision;
    }

    /**
     * @param revision
     *                 the revision to set
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
     *                     the topLevelItem to set
     */
    public void setTopLevelItem(String topLevelItem) {
        this.topLevelItem = topLevelItem;
    }

    /**
     * @return the multipleItemNumbers
     */
    public String getMultipleItemNumbers() {
        return multipleItemNumbers;
    }

    /**
     * @param multipleItemNumbers
     *                            the multipleItemNumbers to set
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
     *               the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the replacePending
     */
    public String getReplacePending() {
        return replacePending;
    }

    /**
     * @param replacePending
     *                       the replacePending to set
     */
    public void setReplacePending(String replacePending) {
        this.replacePending = replacePending;
    }

    /**
     * @return the assigned
     */
    public String getAssigned() {
        return assigned;
    }

    /**
     * @param assigned
     *                 the assigned to set
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
     *                       the responsibility to set
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
     *                   the assignedTo to set
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
     *                 the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the usedAsSubassembly
     */
    public String getUsedAsSubassembly() {
        return usedAsSubassembly;
    }

    /**
     * @param usedAsSubassembly
     *                          the usedAsSubassembly to set
     */
    public void setUsedAsSubassembly(String usedAsSubassembly) {
        this.usedAsSubassembly = usedAsSubassembly;
    }

    /**
     * @return the repairs
     */
    public String getRepairs() {
        return repairs;
    }

    /**
     * @param repairs
     *                the repairs to set
     */
    public void setRepairs(String repairs) {
        this.repairs = repairs;
    }

   
}

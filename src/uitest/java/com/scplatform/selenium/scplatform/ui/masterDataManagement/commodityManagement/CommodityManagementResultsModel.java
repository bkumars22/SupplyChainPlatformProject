/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.annotations.DisplayName;
import com.test.selenium.common.modelViewController.model.Model;

public class CommodityManagementResultsModel extends Model {

    private static final long serialVersionUID = 1L;

    @AutoPopulateOff
    @DisplayName("Commodity Name")
    private String commodityName;

    @AutoPopulateOff
    @DisplayName("Managed By")
    private String managedBy;

    @AutoPopulateOff
    @DisplayName("Assigned To")
    private String assignedTo;

    @AutoPopulateOff
    @DisplayName("Responsibility")
    private String responsibility = "OWNER";

    private String applyToAllResults;
    private String assignTo;
    private String assignManagedBy;

    public String getManagedBy() {
        // if ((managedBy == null) && (assignManagedBy != null)) {
        // if (assignManagedBy.equals("EM Managed")) {
        // managedBy = "EM";
        // } else {
        // managedBy = assignManagedBy;
        // }
        // } else {
        // if (managedBy.equals("EM Managed")) {
        // managedBy = "EM";
        // }
        // }
        return managedBy;
    }

    public String getResponsibility() {
        if (responsibility == null) {
            responsibility = "OWNER";
        }
        return responsibility;
    }

    public String getApplyToAllResults() {
        if (StringUtils.isBlank(applyToAllResults)) {
            applyToAllResults = "false";
        }
        return applyToAllResults.toLowerCase();
    }

    public boolean isApplyToAllResults() {
        return getApplyToAllResults().equals("true");
    }

    /**
     * @return the commodityName
     */
    public String getCommodityName() {
        return commodityName;
    }

    /**
     * @param commodityName
     *            the commodityName to set
     */
    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
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
     * @return the assignTo
     */
    public String getAssignTo() {
        return assignTo;
    }

    /**
     * @param assignTo
     *            the assignTo to set
     */
    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    /**
     * @return the assignManagedBy
     */
    public String getAssignManagedBy() {
        return assignManagedBy;
    }

    /**
     * @param assignManagedBy
     *            the assignManagedBy to set
     */
    public void setAssignManagedBy(String assignManagedBy) {
        this.assignManagedBy = assignManagedBy;
    }

    /**
     * @param managedBy
     *            the managedBy to set
     */
    public void setManagedBy(String managedBy) {
        this.managedBy = managedBy;
    }

    /**
     * @param responsibility
     *            the responsibility to set
     */
    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    /**
     * @param applyToAllResults
     *            the applyToAllResults to set
     */
    public void setApplyToAllResults(String applyToAllResults) {
        this.applyToAllResults = applyToAllResults;
    }

}

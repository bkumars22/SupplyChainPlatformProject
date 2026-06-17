/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.businessDocument;

import com.test.selenium.common.modelViewController.model.Model;
import com.google.common.base.Preconditions;

/**
 * Navigation: Administration -> Manage Roles
 *
 * @author dgenrich
 */
public class BusinessDocumentModel extends Model {

    // TODO: Add in all details for each section

    private static final long serialVersionUID = 1L;

    private String sourcingLane_setAll;
    private String costRecord_setAll;
    private String rebate_setAll;
    private String supplyAllocation_setAll;
    private String forecast_setAll;
    private String itemAssignment_setAll;
    private String itemCommodity_setAll;
    private String billOfMaterial_setAll;
    private String uploadAndDownload_setAll;
    private String uploadDocuments_setAll;
    private String reports_setAll;

    public String getSourcingLane_setAll() {
        return sourcingLane_setAll;
    }

    public void setSourcingLane_setAll(String sourcingLane_setAll) {
        checkParameter("sourcingLane_setAll", sourcingLane_setAll);
        this.sourcingLane_setAll = sourcingLane_setAll.toLowerCase();
    }

    public String getCostRecord_setAll() {
        return costRecord_setAll;
    }

    public void setCostRecord_setAll(String costRecord_setAll) {
        checkParameter("costRecord_setAll", costRecord_setAll);
        this.costRecord_setAll = costRecord_setAll.toLowerCase();
    }

    public String getRebate_setAll() {
        return rebate_setAll;
    }

    public void setRebate_setAll(String rebate_setAll) {
        checkParameter("rebate_setAll", rebate_setAll);
        this.rebate_setAll = rebate_setAll.toLowerCase();
    }

    public String getSupplyAllocation_setAll() {
        return supplyAllocation_setAll;
    }

    public void setSupplyAllocation_setAll(String supplyAllocation_setAll) {
        checkParameter("supplyAllocation_setAll", supplyAllocation_setAll);
        this.supplyAllocation_setAll = supplyAllocation_setAll.toLowerCase();
    }

    public String getForecast_setAll() {
        return forecast_setAll;
    }

    public void setForecast_setAll(String forecast_setAll) {
        checkParameter("forecast_setAll", forecast_setAll);
        this.forecast_setAll = forecast_setAll.toLowerCase();
    }

    public String getItemAssignment_setAll() {
        return itemAssignment_setAll;
    }

    public void setItemAssignment_setAll(String itemAssignment_setAll) {
        checkParameter("itemAssignment_setAll", itemAssignment_setAll);
        this.itemAssignment_setAll = itemAssignment_setAll.toLowerCase();
    }

    public String getItemCommodity_setAll() {
        return itemCommodity_setAll;
    }

    public void setItemCommodity_setAll(String itemCommodity_setAll) {
        checkParameter("itemCommodity_setAll", itemCommodity_setAll);
        this.itemCommodity_setAll = itemCommodity_setAll.toLowerCase();
    }

    public String getBillOfMaterial_setAll() {
        return billOfMaterial_setAll;
    }

    public void setBillOfMaterial_setAll(String billOfMaterial_setAll) {
        checkParameter("billOfMaterial_setAll", billOfMaterial_setAll);
        this.billOfMaterial_setAll = billOfMaterial_setAll.toLowerCase();
    }

    public String getUploadAndDownload_setAll() {
        return uploadAndDownload_setAll;
    }

    public void setUploadAndDownload_setAll(String uploadAndDownload_setAll) {
        checkParameter("uploadAndDownload_setAll", uploadAndDownload_setAll);
        this.uploadAndDownload_setAll = uploadAndDownload_setAll.toLowerCase();
    }

    public String getUploadDocuments_setAll() {
        return uploadDocuments_setAll;
    }

    public void setUploadDocuments_setAll(String uploadDocuments_setAll) {
        checkParameter("uploadDocuments_setAll", uploadDocuments_setAll);
        this.uploadDocuments_setAll = uploadDocuments_setAll.toLowerCase();
    }

    public String getReports_setAll() {
        return reports_setAll;
    }

    public void setReports_setAll(String reports_setAll) {
        checkParameter("reports_setAll", reports_setAll);
        this.reports_setAll = reports_setAll.toLowerCase();
    }

    private void checkParameter(String subVaraible, String subValue) {
        Preconditions.checkArgument(
                (("true".equals(subValue.toLowerCase())) || ("false".equals(subValue.toLowerCase()))),
                "Invalid value for %s (%s).  %s can only be 'true' or 'false'", subVaraible, subValue, subVaraible);
    }
}

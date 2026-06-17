/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.workflow;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.modelViewController.annotations.AutoPopulateOff;
import com.test.selenium.common.modelViewController.model.Model;
import com.google.common.base.Preconditions;

/**
 * Navigation: Administration -> Manage Roles
 *
 * @author dgenrich
 */
public class WorkflowModel extends Model {

    private static final long serialVersionUID = 1L;

    private String executeAnyWorkflow;

    private String main;
    private String main_Dashboard;
    private String main_Upload;
    private String main_ManageUploadJobs;
    private String main_ReviewAlerts;
    @AutoPopulateOff
    private boolean open_main = false;

    private String costing;
    private String costing_NewSourcingLane;
    private String costing_SearchSourcingLane;
    private String costing_SearchCostRecords;
    private String costing_NewCommodityCodeCostRecord;
    private String costing_SearchCommodityCodeCostRecords;
    @AutoPopulateOff
    private boolean open_costing = false;

    private String rebates;
    private String rebates_NewRebateProgram;
    private String rebates_SearchRebateProgram;
    @AutoPopulateOff
    private boolean open_rebates = false;

    private String supplyAllocation;
    private String supplyAllocation_NewSupplyAllocation;
    private String supplyAllocation_SearchSupplyAllocation;
    @AutoPopulateOff
    private boolean open_supplyAllocation = false;

    private String masterDataManagement;
    private String masterDataManagement_ItemAssignment;
    private String masterDataManagement_CommodityManagement;
    private String masterDataManagement_BOMManagement;
    @AutoPopulateOff
    private boolean open_masterDataManagement = false;

    private String forecast;
    private String forecast_NewForecast;
    private String forecast_SearchForecast;
    @AutoPopulateOff
    private boolean open_forecast = false;

    private String reports;
    private String reports_SubmitViewReports;
    private String reports_SellPriceReport;
    @AutoPopulateOff
    private boolean open_reports = false;

    private String search;
    private String search_Items;
    private String search_ItemAVL;
    private String search_BOMs;
    @AutoPopulateOff
    private boolean open_search = false;

    private String administration;
    private String administration_EditProfile;
    private String administration_ChangeDashboardNews;
    private String administration_ManageAlerts;
    private String administration_ManageItems;
    private String administration_ManageRoles;
    private String administration_ManageContacts;
    private String administration_ManageBusinessEntities;
    private String administration_ManageUsers;
    private String administration_AuditHistory;
    private String administration_AdminUpload;
    @AutoPopulateOff
    private boolean open_administration = false;

    public String getExecuteAnyWorkflow() {
        return executeAnyWorkflow;
    }

    public boolean isExecuteAnyWorkflow() {
        boolean execute = false;
        if (StringUtils.isNotBlank(this.executeAnyWorkflow)) {
            execute = "true".equals(this.executeAnyWorkflow);
        }
        return execute;
    }

    public void setExecuteAnyWorkflow(String executeAnyWorkflow) {
        Preconditions.checkArgument(
                (("true".equals(executeAnyWorkflow.toLowerCase()))
                        || ("false".equals(executeAnyWorkflow.toLowerCase()))),
                "Invalid value for executeAnyWorkflow (%s).  executeAnyWorkflow can only be 'true' or 'false'",
                executeAnyWorkflow);

        this.executeAnyWorkflow = executeAnyWorkflow.toLowerCase();
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        checkTopLevelParameter("main", main);
        this.main = main;
    }

    public String getMain_Dashboard() {
        return main_Dashboard;
    }

    public void setMain_Dashboard(String main_Dashboard) {
        checkSubParameter("main_Dashboard", main_Dashboard);
        this.main_Dashboard = main_Dashboard;
    }

    public String getMain_Upload() {
        return main_Upload;
    }

    public void setMain_Upload(String main_Upload) {
        checkSubParameter("main_Upload", main_Upload);
        this.main_Upload = main_Upload;
    }

    public String getMain_ManageUploadJobs() {
        return main_ManageUploadJobs;
    }

    public void setMain_ManageUploadJobs(String main_ManageUploadJobs) {
        checkSubParameter("main_ManageUploadJobs", main_ManageUploadJobs);
        this.open_main = true;
        this.main_ManageUploadJobs = main_ManageUploadJobs;
    }

    public String getMain_ReviewAlerts() {
        return main_ReviewAlerts;
    }

    public void setMain_ReviewAlerts(String main_ReviewAlerts) {
        checkSubParameter("main_ReviewAlerts", main_ReviewAlerts);
        this.open_main = true;
        this.main_ReviewAlerts = main_ReviewAlerts;
    }

    public String getCosting() {
        return costing;
    }

    public void setCosting(String costing) {
        checkTopLevelParameter("costing", costing);
        this.costing = costing;
    }

    public String getCosting_NewSourcingLane() {
        return costing_NewSourcingLane;
    }

    public void setCosting_NewSourcingLane(String costing_NewSourcingLane) {
        checkSubParameter("costing_NewSourcingLane", costing_NewSourcingLane);
        this.open_costing = true;
        this.costing_NewSourcingLane = costing_NewSourcingLane;
    }

    public String getCosting_SearchSourcingLane() {
        return costing_SearchSourcingLane;
    }

    public void setCosting_SearchSourcingLane(String costing_SearchSourcingLane) {
        checkSubParameter("costing_SearchSourcingLane", costing_SearchSourcingLane);
        this.open_costing = true;
        this.costing_SearchSourcingLane = costing_SearchSourcingLane;
    }

    public String getCosting_SearchCostRecords() {
        return costing_SearchCostRecords;
    }

    public void setCosting_SearchCostRecords(String costing_SearchCostRecords) {
        checkSubParameter("costing_SearchCostRecords", costing_SearchCostRecords);
        this.open_costing = true;
        this.costing_SearchCostRecords = costing_SearchCostRecords;
    }

    public String getCosting_NewCommodityCodeCostRecord() {
        return costing_NewCommodityCodeCostRecord;
    }

    public void setCosting_NewCommodityCodeCostRecord(String costing_NewCommodityCodeCostRecord) {
        checkSubParameter("costing_NewCommodityCodeCostRecord", costing_NewCommodityCodeCostRecord);
        this.open_costing = true;
        this.costing_NewCommodityCodeCostRecord = costing_NewCommodityCodeCostRecord;
    }

    public String getCosting_SearchCommodityCodeCostRecords() {
        return costing_SearchCommodityCodeCostRecords;
    }

    public void setCosting_SearchCommodityCodeCostRecords(String costing_SearchCommodityCodeCostRecords) {
        checkSubParameter("costing_SearchCommodityCodeCostRecords", costing_SearchCommodityCodeCostRecords);
        this.open_costing = true;
        this.costing_SearchCommodityCodeCostRecords = costing_SearchCommodityCodeCostRecords;
    }

    public String getRebates() {
        return rebates;
    }

    public void setRebates(String rebates) {
        checkTopLevelParameter("rebates", rebates);
        this.rebates = rebates;
    }

    public String getRebates_NewRebateProgram() {
        return rebates_NewRebateProgram;
    }

    public void setRebates_NewRebateProgram(String rebates_NewRebateProgram) {
        checkSubParameter("rebates_NewRebateProgram", rebates_NewRebateProgram);
        this.open_rebates = true;
        this.rebates_NewRebateProgram = rebates_NewRebateProgram;
    }

    public String getRebates_SearchRebateProgram() {
        return rebates_SearchRebateProgram;
    }

    public void setRebates_SearchRebateProgram(String rebates_SearchRebateProgram) {
        checkSubParameter("rebates_SearchRebateProgram", rebates_SearchRebateProgram);
        this.open_rebates = true;
        this.rebates_SearchRebateProgram = rebates_SearchRebateProgram;
    }

    public String getSupplyAllocation() {
        return supplyAllocation;
    }

    public void setSupplyAllocation(String supplyAllocation) {
        checkTopLevelParameter("supplyAllocation", supplyAllocation);
        this.supplyAllocation = supplyAllocation;
    }

    public String getSupplyAllocation_NewSupplyAllocation() {
        return supplyAllocation_NewSupplyAllocation;
    }

    public void setSupplyAllocation_NewSupplyAllocation(String supplyAllocation_NewSupplyAllocation) {
        checkSubParameter("supplyAllocation_NewSupplyAllocation", supplyAllocation_NewSupplyAllocation);
        this.open_supplyAllocation = true;
        this.supplyAllocation_NewSupplyAllocation = supplyAllocation_NewSupplyAllocation;
    }

    public String getSupplyAllocation_SearchSupplyAllocation() {
        return supplyAllocation_SearchSupplyAllocation;
    }

    public void setSupplyAllocation_SearchSupplyAllocation(String supplyAllocation_SearchSupplyAllocation) {
        checkSubParameter("supplyAllocation_SearchSupplyAllocation", supplyAllocation_SearchSupplyAllocation);
        this.open_supplyAllocation = true;
        this.supplyAllocation_SearchSupplyAllocation = supplyAllocation_SearchSupplyAllocation;
    }

    public String getMasterDataManagement() {
        return masterDataManagement;
    }

    public void setMasterDataManagement(String masterDataManagement) {
        checkTopLevelParameter("masterDataManagement", masterDataManagement);
        this.masterDataManagement = masterDataManagement;
    }

    public String getMasterDataManagement_ItemAssignment() {
        return masterDataManagement_ItemAssignment;
    }

    public void setMasterDataManagement_ItemAssignment(String masterDataManagement_ItemAssignment) {
        checkSubParameter("masterDataManagement_ItemAssignment", masterDataManagement_ItemAssignment);
        this.open_masterDataManagement = true;
        this.masterDataManagement_ItemAssignment = masterDataManagement_ItemAssignment;
    }

    public String getMasterDataManagement_CommodityManagement() {
        return masterDataManagement_CommodityManagement;
    }

    public void setMasterDataManagement_CommodityManagement(String masterDataManagement_CommodityManagement) {
        checkSubParameter("masterDataManagement_CommodityManagement", masterDataManagement_CommodityManagement);
        this.open_masterDataManagement = true;
        this.masterDataManagement_CommodityManagement = masterDataManagement_CommodityManagement;
    }

    public String getMasterDataManagement_BOMManagement() {
        return masterDataManagement_BOMManagement;
    }

    public void setMasterDataManagement_BOMManagement(String masterDataManagement_BOMManagement) {
        checkSubParameter("masterDataManagement_BOMManagement", masterDataManagement_BOMManagement);
        this.open_masterDataManagement = true;
        this.masterDataManagement_BOMManagement = masterDataManagement_BOMManagement;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        checkTopLevelParameter("forecast", forecast);
        this.forecast = forecast;
    }

    public String getForecast_NewForecast() {
        return forecast_NewForecast;
    }

    public void setForecast_NewForecast(String forecast_NewForecast) {
        checkSubParameter("forecast_NewForecast", forecast_NewForecast);
        this.open_forecast = true;
        this.forecast_NewForecast = forecast_NewForecast;
    }

    public String getForecast_SearchForecast() {
        return forecast_SearchForecast;
    }

    public void setForecast_SearchForecast(String forecast_SearchForecast) {
        checkSubParameter("forecast_SearchForecast", forecast_SearchForecast);
        this.open_forecast = true;
        this.forecast_SearchForecast = forecast_SearchForecast;
    }

    public String getReport() {
        return reports;
    }

    public void setReport(String reports) {
        checkTopLevelParameter("reports", reports);
        this.reports = reports;
    }

    public String getReport_SubmitViewReports() {
        return reports_SubmitViewReports;
    }

    public void setReport_SubmitViewReports(String reports_SubmitViewReports) {
        checkSubParameter("reports_SubmitViewReports", reports_SubmitViewReports);
        this.open_reports = true;
        this.reports_SubmitViewReports = reports_SubmitViewReports;
    }

    public String getReport_SellPriceReport() {
        return reports_SellPriceReport;
    }

    public void setReport_SellPriceReport(String reports_SellPriceReport) {
        checkSubParameter("reports_SellPriceReport", reports_SellPriceReport);
        this.open_reports = true;
        this.reports_SellPriceReport = reports_SellPriceReport;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        checkTopLevelParameter("search", search);
        this.search = search;
    }

    public String getSearch_Items() {
        return search_Items;
    }

    public void setSearch_Items(String search_Items) {
        checkSubParameter("search_Items", search_Items);
        this.open_search = true;
        this.search_Items = search_Items;
    }

    public String getSearch_ItemAVL() {
        return search_ItemAVL;
    }

    public void setSearch_ItemAVL(String search_ItemAVL) {
        checkSubParameter("search_ItemAVL", search_ItemAVL);
        this.open_search = true;
        this.search_ItemAVL = search_ItemAVL;
    }

    public String getSearch_BOMs() {
        return search_BOMs;
    }

    public void setSearch_BOMs(String search_BOMs) {
        checkSubParameter("search_BOMs", search_BOMs);
        this.open_search = true;
        this.search_BOMs = search_BOMs;
    }

    public String getAdministration() {
        return administration;
    }

    public void setAdministration(String administration) {
        checkTopLevelParameter("administration", administration);
        this.open_administration = true;
        this.administration = administration;
    }

    public String getAdministration_EditProfile() {
        return administration_EditProfile;
    }

    public void setAdministration_EditProfile(String administration_EditProfile) {
        checkSubParameter("administration_EditProfile", administration_EditProfile);
        this.open_administration = true;
        this.administration_EditProfile = administration_EditProfile;
    }

    public String getAdministration_ChangeDashboardNews() {
        return administration_ChangeDashboardNews;
    }

    public void setAdministration_ChangeDashboardNews(String administration_ChangeDashboardNews) {
        checkSubParameter("administration_ChangeDashboardNews", administration_ChangeDashboardNews);
        this.open_administration = true;
        this.administration_ChangeDashboardNews = administration_ChangeDashboardNews;
    }

    public String getAdministration_ManageAlerts() {
        return administration_ManageAlerts;
    }

    public void setAdministration_ManageAlerts(String administration_ManageAlerts) {
        checkSubParameter("administration_ManageAlerts", administration_ManageAlerts);
        this.open_administration = true;
        this.administration_ManageAlerts = administration_ManageAlerts;
    }

    public String getAdministration_ManageItems() {
        return administration_ManageItems;
    }

    public void setAdministration_ManageItems(String administration_ManageItems) {
        checkSubParameter("administration_ManageItems", administration_ManageItems);
        this.open_administration = true;
        this.administration_ManageItems = administration_ManageItems;
    }

    public String getAdministration_ManageRoles() {
        return administration_ManageRoles;
    }

    public void setAdministration_ManageRoles(String administration_ManageRoles) {
        checkSubParameter("administration_ManageRoles", administration_ManageRoles);
        this.open_administration = true;
        this.administration_ManageRoles = administration_ManageRoles;
    }

    public String getAdministration_ManageContacts() {
        return administration_ManageContacts;
    }

    public void setAdministration_ManageContacts(String administration_ManageContacts) {
        checkSubParameter("administration_ManageContacts", administration_ManageContacts);
        this.open_administration = true;
        this.administration_ManageContacts = administration_ManageContacts;
    }

    public String getAdministration_ManageBusinessEntities() {
        return administration_ManageBusinessEntities;
    }

    public void setAdministration_ManageBusinessEntities(String administration_ManageBusinessEntities) {
        checkSubParameter("administration_ManageBusinessEntities", administration_ManageBusinessEntities);
        this.open_administration = true;
        this.administration_ManageBusinessEntities = administration_ManageBusinessEntities;
    }

    public String getAdministration_ManageUsers() {
        return administration_ManageUsers;
    }

    public void setAdministration_ManageUsers(String administration_ManageUsers) {
        checkSubParameter("administration_ManageUsers", administration_ManageUsers);
        this.open_administration = true;
        this.administration_ManageUsers = administration_ManageUsers;
    }

    public String getAdministration_AuditHistory() {
        return administration_AuditHistory;
    }

    public void setAdministration_AuditHistory(String administration_AuditHistory) {
        checkSubParameter("administration_AuditHistory", administration_AuditHistory);
        this.open_administration = true;
        this.administration_AuditHistory = administration_AuditHistory;
    }

    public String getAdministration_AdminUpload() {
        return administration_AdminUpload;
    }

    public void setAdministration_AdminUpload(String administration_AdminUpload) {
        checkSubParameter("administration_AdminUpload", administration_AdminUpload);
        this.open_administration = true;
        this.administration_AdminUpload = administration_AdminUpload;
    }

    public boolean open_main() {
        return open_main;
    }

    public boolean open_costing() {
        return open_costing;
    }

    public boolean open_rebates() {
        return open_rebates;
    }

    public boolean open_supplyAllocation() {
        return open_supplyAllocation;
    }

    public boolean open_masterDataManagement() {
        return open_masterDataManagement;
    }

    public boolean open_forecast() {
        return open_forecast;
    }

    public boolean open_reports() {
        return open_reports;
    }

    public boolean open_search() {
        return open_search;
    }

    public boolean open_administration() {
        return open_administration;
    }

    /**
     * @return the open_main
     */
    public boolean isOpen_main() {
        return open_main;
    }

    /**
     * @param open_main
     *            the open_main to set
     */
    public void setOpen_main(boolean open_main) {
        this.open_main = open_main;
    }

    /**
     * @return the open_costing
     */
    public boolean isOpen_costing() {
        return open_costing;
    }

    /**
     * @param open_costing
     *            the open_costing to set
     */
    public void setOpen_costing(boolean open_costing) {
        this.open_costing = open_costing;
    }

    /**
     * @return the open_rebates
     */
    public boolean isOpen_rebates() {
        return open_rebates;
    }

    /**
     * @param open_rebates
     *            the open_rebates to set
     */
    public void setOpen_rebates(boolean open_rebates) {
        this.open_rebates = open_rebates;
    }

    /**
     * @return the open_supplyAllocation
     */
    public boolean isOpen_supplyAllocation() {
        return open_supplyAllocation;
    }

    /**
     * @param open_supplyAllocation
     *            the open_supplyAllocation to set
     */
    public void setOpen_supplyAllocation(boolean open_supplyAllocation) {
        this.open_supplyAllocation = open_supplyAllocation;
    }

    /**
     * @return the open_masterDataManagement
     */
    public boolean isOpen_masterDataManagement() {
        return open_masterDataManagement;
    }

    /**
     * @param open_masterDataManagement
     *            the open_masterDataManagement to set
     */
    public void setOpen_masterDataManagement(boolean open_masterDataManagement) {
        this.open_masterDataManagement = open_masterDataManagement;
    }

    /**
     * @return the open_forecast
     */
    public boolean isOpen_forecast() {
        return open_forecast;
    }

    /**
     * @param open_forecast
     *            the open_forecast to set
     */
    public void setOpen_forecast(boolean open_forecast) {
        this.open_forecast = open_forecast;
    }

    /**
     * @return the reports
     */
    public String getReports() {
        return reports;
    }

    /**
     * @param reports
     *            the reports to set
     */
    public void setReports(String reports) {
        this.reports = reports;
    }

    /**
     * @return the reports_SubmitViewReports
     */
    public String getReports_SubmitViewReports() {
        return reports_SubmitViewReports;
    }

    /**
     * @param reports_SubmitViewReports
     *            the reports_SubmitViewReports to set
     */
    public void setReports_SubmitViewReports(String reports_SubmitViewReports) {
        this.reports_SubmitViewReports = reports_SubmitViewReports;
    }

    /**
     * @return the reports_SellPriceReport
     */
    public String getReports_SellPriceReport() {
        return reports_SellPriceReport;
    }

    /**
     * @param reports_SellPriceReport
     *            the reports_SellPriceReport to set
     */
    public void setReports_SellPriceReport(String reports_SellPriceReport) {
        this.reports_SellPriceReport = reports_SellPriceReport;
    }

    /**
     * @return the open_reports
     */
    public boolean isOpen_reports() {
        return open_reports;
    }

    /**
     * @param open_reports
     *            the open_reports to set
     */
    public void setOpen_reports(boolean open_reports) {
        this.open_reports = open_reports;
    }

    /**
     * @return the open_search
     */
    public boolean isOpen_search() {
        return open_search;
    }

    /**
     * @param open_search
     *            the open_search to set
     */
    public void setOpen_search(boolean open_search) {
        this.open_search = open_search;
    }

    /**
     * @return the open_administration
     */
    public boolean isOpen_administration() {
        return open_administration;
    }

    /**
     * @param open_administration
     *            the open_administration to set
     */
    public void setOpen_administration(boolean open_administration) {
        this.open_administration = open_administration;
    }

    private void checkTopLevelParameter(String variableName, String variableValue) {
        List<String> acceptedValues = new ArrayList<String>();
        acceptedValues.add("No Access");
        acceptedValues.add("Execute");
        acceptedValues.add("Execute All");

        Preconditions.checkArgument(acceptedValues.contains(variableValue),
                "Invalid value for %s (%s).  %s must be one of: %s", variableName, variableValue, variableName,
                acceptedValues.toString());
    }

    private void checkSubParameter(String subVaraible, String subValue) {
        List<String> acceptedValues = new ArrayList<String>();
        acceptedValues.add("");
        acceptedValues.add("Execute");

        Preconditions.checkArgument(acceptedValues.contains(subValue),
                "Invalid value for %s (%s).  %s must be one of: %s", subVaraible, subValue, subVaraible,
                acceptedValues.toString());
    }
}

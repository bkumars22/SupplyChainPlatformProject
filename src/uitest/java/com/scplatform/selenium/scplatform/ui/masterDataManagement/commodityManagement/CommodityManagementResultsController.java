/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.google.common.base.Preconditions;

/**
 * DOCUMENTATION:
 * http://confluence.dev.scplatform.local/display/QA/Commodity+Management
 *
 * @author dgenrich
 *
 */
public class CommodityManagementResultsController extends SCPlatformSearchResultsController {
    protected static List<CommodityManagementResultsModel> commodityManagementResultsModel = null;
    protected CommodityManagementResultsPage page;
    protected By tableLocator;

    public CommodityManagementResultsController() {
        super();
        page = new CommodityManagementResultsPage();
        tableLocator = page.tableLocator();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new CommodityManagementResultsPage();
        }
        return page;
    }

    public void assign() throws Exception {
        super.assertModelsExist();
        getView();

        setPageSize(100);
        intializeModel();
        applyToAllResults();
        assignTo();
        managedBy();
    }

    public boolean validate() {
        Preconditions.checkNotNull(commodityManagementResultsModel,
                "The commodityManagementResultsModel data is null.  This gets set when a 'assign()' is done");

        getView();
        boolean success = true;
        boolean verified = true;
        List<CommodityManagementResultsModel> actualModel = page.parseResults();

        for (CommodityManagementResultsModel expected : commodityManagementResultsModel) {
            CommodityManagementResultsModel actual = findActual(actualModel, expected);
            if (actual == null) {
                JLog.error("Unable to find actual Commodity Name: " + expected.getCommodityName(), TakeScreenshot.True);
                continue;
            }

            verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
                    expected.getCommodityName());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("managedBy"), actual.getManagedBy(), expected.getManagedBy());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("assignedTo"), actual.getAssignedTo(), expected.getAssignedTo());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                    expected.getResponsibility());
            success = (verified) ? success : verified;

            JLog.blankLine();
        }

        return success;
    }

    protected CommodityManagementResultsModel findActual(List<CommodityManagementResultsModel> actualModel,
            CommodityManagementResultsModel expected) {

        for (CommodityManagementResultsModel actual : actualModel) {
            if (actual.getCommodityName().equals(expected.getCommodityName())) {
                return actual;
            }
        }
        return null;
    }

    protected void managedBy() {
        List<String> uniqueManagedBy = new ArrayList<String>();
        CommodityManagementResultsPage page = new CommodityManagementResultsPage();
        boolean performAction = false;

        // find the unique assign to names
        for (Model model : models) {
            if (!uniqueManagedBy.contains(((CommodityManagementResultsModel) model).getAssignManagedBy())) {
                uniqueManagedBy.add(((CommodityManagementResultsModel) model).getAssignManagedBy());
            }
        }

        // do the assignment
        for (String managedBy : uniqueManagedBy) {
            for (Model model : models) {
                if (((CommodityManagementResultsModel) model).getAssignManagedBy() != null) {
                    if (managedBy.equals(((CommodityManagementResultsModel) model).getAssignManagedBy())) {
                        check(((CommodityManagementResultsModel) model).getCommodityName(), tableLocator);
                        updateModel(((CommodityManagementResultsModel) model).getCommodityName(), null, managedBy);
                        performAction = true;
                    }
                }
            }

            if (performAction) {
                setValue(page.assignManagedBy(), managedBy);
                clickAndCheckForPOSTError(page.setButton());
            }
        }

    }

    protected void assignTo() {
        List<String> uniqueAssignTo = new ArrayList<String>();
        CommodityManagementResultsPage page = new CommodityManagementResultsPage();
        boolean performAction = false;

        // find the unique assign to names
        for (Model model : models) {
            if (!uniqueAssignTo.contains(((CommodityManagementResultsModel) model).getAssignTo())) {
                uniqueAssignTo.add(((CommodityManagementResultsModel) model).getAssignTo());
            }
        }

        // do the assignment
        for (String assignTo : uniqueAssignTo) {
            for (Model model : models) {
                if (((CommodityManagementResultsModel) model).getAssignTo() != null) {
                    if (assignTo.equals(((CommodityManagementResultsModel) model).getAssignTo())) {
                        check(((CommodityManagementResultsModel) model).getCommodityName(), tableLocator);
                        updateModel(((CommodityManagementResultsModel) model).getCommodityName(), assignTo, null);
                        performAction = true;
                    }
                }

            }

            if (performAction) {
                setValue(page.assignTo(), assignTo);
                clickAndCheckForPOSTError(page.assignButton());
            }

        }

    }

    protected void applyToAllResults() {
        String assignToName = null;
        String assignManagedBy = null;
        boolean applyToAllResults = false;

        for (Model model : models) {
            if (((CommodityManagementResultsModel) model).isApplyToAllResults()) {
                applyToAllResults = true;
                if (StringUtils.isNotBlank(((CommodityManagementResultsModel) model).getAssignManagedBy())) {
                    assignManagedBy = ((CommodityManagementResultsModel) model).getAssignManagedBy();
                }

                if (((CommodityManagementResultsModel) model).getAssignTo() != null) {
                    assignToName = ((CommodityManagementResultsModel) model).getAssignTo();
                }

            }
        }

        if (applyToAllResults) {
            CommodityManagementResultsPage page = new CommodityManagementResultsPage();
            setValue(page.applyToAllResults(), true);

            if (assignToName != null) {
                setValue(page.assignTo(), assignToName);
                clickAndCheckForPOSTError(page.assignButton());
            }

            if (assignManagedBy != null) {
                setValue(page.assignManagedBy(), assignManagedBy);
                clickAndCheckForPOSTError(page.setButton());
            }
            updateModel(null, assignToName, assignManagedBy);
        }

    }

    protected void updateModel(String commodityName, String assignToName, String assignManagedBy) {
        for (int i = 0; i < commodityManagementResultsModel.size(); i++) {
            boolean doUpdate = false;
            if (commodityName == null) {
                // update all models
                doUpdate = true;
            } else if (commodityManagementResultsModel.get(i).getCommodityName().equals(commodityName)) {
                doUpdate = true;
            }

            if (doUpdate) {
                if (assignToName != null) {
                    commodityManagementResultsModel.get(i).setAssignedTo(assignToName);
                }

                if (assignManagedBy != null) {
                    commodityManagementResultsModel.get(i).setManagedBy(assignManagedBy);
                }
            }
        }
    }

    protected void intializeModel() {
        if (commodityManagementResultsModel == null) {
            commodityManagementResultsModel = new ArrayList<CommodityManagementResultsModel>();

            for (Model model : models) {
                commodityManagementResultsModel.add((CommodityManagementResultsModel) model);
            }
        }

    }
}

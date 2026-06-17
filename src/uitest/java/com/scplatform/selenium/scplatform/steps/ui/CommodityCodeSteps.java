/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement.CommodityManagementController;
import com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement.CommodityManagementModel;
import com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement.CommodityManagementResultsController;
import com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement.CommodityManagementResultsModel;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;

/**
 * DOCUMENTATION:
 * http://confluence.dev.scplatform.local/display/QA/Commodity+Management
 *
 * @author dgenrich
 *
 */
public class CommodityCodeSteps {
    protected SCPlatformNavigation nav;

    @Before
    public void beforeMethod(Scenario scenario) {
        JLog.setScenarioForCucumber(scenario);
        JLog.resetErrorCount();
        nav = new SCPlatformNavigation();
    }

    private void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
        }
    }

    @Given("I update the Commodity Code data through Commodity Management with the following data")
    public void updateCommodityCode(List<CommodityManagementResultsModel> commodityCodeModel) throws Exception {
        Preprocessing.process(commodityCodeModel);

        nav.CommodityManagement();

        for (CommodityManagementResultsModel model : commodityCodeModel) {
            // search for commodity code
            CommodityManagementModel searchModel = new CommodityManagementModel();
            searchModel.setCommodityName(model.getCommodityName());

            CommodityManagementController commodityManagementController = new CommodityManagementController();
            commodityManagementController.setModel(searchModel);
            commodityManagementController.search();

            // update the commodity code fields
            CommodityManagementResultsController commodityManagementResultsController = new CommodityManagementResultsController();
            commodityManagementResultsController.setModel(model);
            commodityManagementResultsController.assign();
        }

        checkForErrors();
    }

    @Then("I validate the Commodity Codes where updated")
    public void validateCommodityCode() {
        CommodityManagementResultsController commodityManagementResultsController = new CommodityManagementResultsController();
        commodityManagementResultsController.validate();

        checkForErrors();
    }

}

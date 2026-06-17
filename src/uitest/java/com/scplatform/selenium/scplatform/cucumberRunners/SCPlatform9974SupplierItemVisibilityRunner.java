/*
 * SCPlatform-9974: Supplier should not see Item AVL and SA for Items not associated with them
 * MSI 262 Story - Supplier Visibility Enhancement (Cases #2, #2.1, #2.2)
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.cucumberRunners;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.test.selenium.common.cucumber.GenerateCucumberReports.REPORT_TYPE;
import com.test.selenium.scplatform.base.E2CukeBase;

import io.cucumber.testng.CucumberOptions;

@CucumberOptions(plugin = {
        "com.test.selenium.common.cucumber.plugins.TestStepWatch",
        "json:target/cucumber-html-report/scplatform9974_supplier_item_visibility.json",
        "html:target/cucumber-html-report/cucumber.html",
        "junit:target/cucumber-junit-report.xml" }, features = {
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9974_SupplierItemVisibility.feature" }, glue = {
                        "com.test.selenium.common.steps",
                        "com.test.selenium.scplatform.steps" }, tags = "@HarmonySupplierItemVisibility and not (@pending or @bug or @skip)")
public class SCPlatform9974SupplierItemVisibilityRunner extends E2CukeBase {

    @Override
    @BeforeSuite
    public void beforeSuite() {
        super.beforeSuite();

        setReportTitle("SCPlatform-9974 Supplier Item Visibility Workflow Test Suite");
        setReportGenerationType(REPORT_TYPE.GenerateOnly);
        setConfigKeyForStack("stack.mcm");
        setSoftwareVersionsForBuild(new String[] { "scplatform" });
        setSystem();
    }

    @Test
    public void doNothing() {
        // allows the class to be run as a TestNG test
    }
}

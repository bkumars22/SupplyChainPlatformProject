/*
 * SCPlatform-9740, SCPlatform-9408: Commodity and Item Enhancements
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
        "json:target/cucumber-html-report/commodity_enhancements.json",
        "html:target/cucumber-html-report/cucumber.html",
        "junit:target/cucumber-junit-report.xml" }, features = {
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/CommodityEnhancements.feature" }, glue = {
                        "com.test.selenium.common.steps",
                        "com.test.selenium.scplatform.steps" }, tags = "@HarmonyCommodityEnhancements and not (@pending or @bug or @skip)")
public class CommodityEnhancementsWorkflowRunner extends E2CukeBase {

    @Override
    @BeforeSuite
    public void beforeSuite() {
        super.beforeSuite();

        setReportTitle("Commodity Enhancements Workflow Test Suite");
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

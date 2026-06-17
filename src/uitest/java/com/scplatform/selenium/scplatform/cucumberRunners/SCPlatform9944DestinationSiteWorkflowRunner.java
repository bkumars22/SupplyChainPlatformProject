/*
 * SCPlatform-9944: Add Destination Site to Sourcing Lane and Supply Allocation Data
 * Covers: Feature flag UI/download checks (TC-1a, TC-1b, TC-2a, TC-2b, TC-3b, TC-4a, TC-4b),
 *         SA upload workflows (TC-SA-UP1 to TC-SA-UP5), SA download (TC-SA-DL1),
 *         AVL upload (TC-AVL-UP1 to TC-AVL-UP4),
 *         CR MSI upload (TC-CR-MSI-1 to TC-CR-MSI-5),
 *         CR Supplier upload (TC-CR-SUPP-1 to TC-CR-SUPP-6)
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
        "json:target/cucumber-html-report/scplatform9944_destination_site.json",
        "html:target/cucumber-html-report/cucumber.html",
        "junit:target/cucumber-junit-report.xml" }, features = {
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature" }, glue = {
                        "com.test.selenium.common.steps",
                        "com.test.selenium.scplatform.steps",
                        "com.test.selenium.scplatform.hooks" }, tags = "@HarmonySCPlatform9944DestinationSite and not (@pending or @bug or @skip)")
public class SCPlatform9944DestinationSiteWorkflowRunner extends E2CukeBase {

    @Override
    @BeforeSuite
    public void beforeSuite() {
        super.beforeSuite();

        setReportTitle("SCPlatform-9944 Destination Site Workflow Test Suite");
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

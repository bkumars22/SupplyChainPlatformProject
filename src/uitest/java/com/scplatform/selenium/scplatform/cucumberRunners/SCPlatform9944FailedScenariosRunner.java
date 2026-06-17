/*
 * SCPlatform-9944: Rerun of 14 failed scenarios from v24 run
 * Targets exactly the failing lines identified from v24 log (2026-04-23):
 *   TC-SA-UP1 (:90), TC-SA-UP2 (:107), TC-SA-UP3 (:124), TC-SA-UP4 (:141), TC-SA-UP5 (:158)
 *   TC-4a (:167), TC-4b (:180), TC-SA-DL1 (:196), TC-AVL-UP3 (:253)
 *   TC-CR-MSI-1 (:291), TC-CR-MSI-2 (:308), TC-CR-MSI-3 (:325), TC-CR-MSI-4 (:342), TC-CR-MSI-5 (:359)
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
        "json:target/cucumber-html-report/scplatform9944_failed_rerun.json",
        "html:target/cucumber-html-report/cucumber.html",
        "junit:target/cucumber-junit-report.xml" }, features = {
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:90",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:107",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:124",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:141",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:158",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:167",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:180",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:196",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:253",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:291",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:308",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:325",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:342",
                "src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SCPlatform9944_DestinationSite.feature:359" }, glue = {
                        "com.test.selenium.common.steps",
                        "com.test.selenium.scplatform.steps",
                        "com.test.selenium.scplatform.hooks" }, tags = "@HarmonySCPlatform9944DestinationSite and not (@pending or @bug or @skip)")
public class SCPlatform9944FailedScenariosRunner extends E2CukeBase {

    @Override
    @BeforeSuite
    public void beforeSuite() {
        super.beforeSuite();

        setReportTitle("SCPlatform-9944 Failed Scenarios Rerun (v24 → v25)");
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

/*
 *
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


@CucumberOptions(plugin = { "json:target/cucumber-html-report/search.json", "html:target/cucumber-html-report/cucumber.html",
"junit:target/cucumber-junit-report.xml" }, features = {
"src/test/resources/com/scplatform/selenium/scplatform/cucumberFeatures/SearchAndMasterDataManagement.feature" }, glue = {
    "com.test.selenium.common.steps",
"com.test.selenium.scplatform.steps"},
    tags = "@HarmonySearch and not (@pending or @bug or @skip)")
public class SearchAndMasterDataManagementWorkflowRunner extends E2CukeBase {

  @Override
  @BeforeSuite
  public void beforeSuite() {
    super.beforeSuite();

    // --- Setup for Report --
    setReportTitle("Search And Master Data Management Workflow Test Plan");
    setReportGenerationType(REPORT_TYPE.GenerateOnly);
    //setAdditionalRecipients("dgenrich@scplatform.local");
    setConfigKeyForStack("stack.mcm");
    setSoftwareVersionsForBuild(new String[] { "scplatform" });
    setSystem();

    //ConsoleThemeHelper.setE2NAStack();
  }

  @Test
  public void doNothing() {
    // this just allows the java class to be ran as a testNG test
  }

}

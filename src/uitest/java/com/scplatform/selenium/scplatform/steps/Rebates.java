/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.text.ParseException;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.MTCMController;
import com.test.selenium.scplatform.modelViewController.RebatesController;
import com.test.selenium.scplatform.modelViewController.SearchController;
import com.google.common.base.Verify;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Rebates {
    SearchController ctrller;

    @Before
    public void beforeMethod(Scenario scenario) {
        JLog.setScenarioForCucumber(scenario);
        JLog.resetErrorCount();
    }

    private void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
        }
    }

    @Then("I verify fields cleared on Search Rebates page")
    public void verifyFieldsClearedOnSearchRebates() {
        RebatesController controller = new RebatesController();
        Verify.verify(controller.getTextFieldValue("name").equals(""), "Rebates Name field is not cleared");
        Verify.verify(controller.getTextFieldValue("financeOwner").equals(""), "financeOwner field is not cleared");
        Verify.verify(controller.getTextFieldValue("programOwner").equals(""), "programOwner field is not cleared");
        JLog.write("Verified that fields are cleared after clicking clear button");
    }
    
    @And("I click on Edit icon on row {string}")
    public void clickEditIcon(String row) {
    	RebatesController controller = new RebatesController();
        controller.clickEditIconOnRow(Integer.parseInt(row));
        JLog.write("Clicked on Edit Icon on row->" + row);
    }

    @Then("I verify rebate amount {string} got deleted")
    public void verifyDeletedAmountIsStillVisible(String expAmt) {
        RebatesController controller = new RebatesController();
        controller.isRebateAmtDisplayed(expAmt);
        JLog.write("Verified that deleted amount is not showing up on the screen");
    }

    @When("I click on the PRICING tab")
    public void clickPricingTab() throws Throwable {
        RebatesController controller = new RebatesController();
        controller.clickPricingTab();
        JLog.write("Clicked on Pricing tab.");
        checkForErrors();
    }

    @When("I click on the View Results tab")
    public void clickViewResultsTab() throws Throwable {
        RebatesController controller = new RebatesController();
        controller.clickViewResultsTab();
        JLog.write("Clicked on View Results tab.");
        checkForErrors();
    }

    @When("I click on the RULES tab")
    public void clickRulesTab() throws Throwable {
        RebatesController controller = new RebatesController();
        controller.clickRulesTab();
        JLog.write("Clicked on Rules tab.");
        checkForErrors();
    }

    @And("I set program end date to {string} days from today")
    public void setDate(String days) {
        RebatesController controller = new RebatesController();
        controller.setEndDate(days);
        JLog.write("Set Program End Date as: " + days + " days from today");
        checkForErrors();
    }

    @When("I set end date as {string} days from start Date")
    public void setToDate(String endDate) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setToDate("today", endDate);
        JLog.write("Set To Date succesfully");
        checkForErrors();
    }

    @When("I set program end date to {string} days ago from today")
    public void setToDateBeforeSTart(String endDate) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setEndBeforeDate(endDate);
        JLog.write("Set To Date succesfully");
        checkForErrors();
    }

    // @When("I set program end date to {string} days from today")
    // public void setToDateAfterSTart(String endDate) throws Throwable {
    // RebatesController ctr = new RebatesController();
    // ctr.setEndDate(endDate);
    // JLog.write("Set To Date succesfully");
    // checkForErrors();
    // }

    @When("I set end date on row {string} as {string} days from start Date")
    public void setToDateOnRow(String row, String endDate) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setToDateOnRow(endDate, row);
        JLog.write("Set To Date succesfully on row - " + row);
        checkForErrors();
    }

    @When("I set start as {string} days from program start date and end dates as {string} days from start date on row {string}")
    public void setDatesWithoutClashOnRow(String startDays, String endDays, String row) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setFromDateFromTodayNoClashForMultipleRebates(startDays, row);
        JLog.screenCapture();
        JLog.write("Set To Date succesfully on row - " + row);
        ctr = new RebatesController();
        ctr.setToDateOnRowWithoutClash(endDays, row);
        JLog.screenCapture();
        JLog.write("Set From Date succesfully on row - " + row);
        checkForErrors();
    }

    @When("I set start date on row {string} as {string} days from program start date")
    public void setFromDateFromToday(String row, String days) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setFromDateFromToday(days, row);
        JLog.write("Set To Date succesfully");
        checkForErrors();
    }

    @When("I set {string} day before program start state as start date")
    public void setFromDate(String day) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setFromDateWithBeforePgmStartDate(day);
        JLog.write("Set the From Date succesfully");
        checkForErrors();
    }

    @When("I set {string} days after rebate end state as start date")
    public void setFromDateAsADayAfterEndDate(String day) throws Throwable {
        RebatesController ctr = new RebatesController();
        ctr.setFromDateAsDayAfterEndDate(day);
        JLog.write("Set the From Date succesfully");
        checkForErrors();
    }

    @And("I enter amount {string} on row {string}")
    public void setAmount(String value, String row) {
        RebatesController controller = new RebatesController();
        controller.setAmountValue(row, value);
        JLog.write("Entered amount value " + value + " on " + row + " row.");
        checkForErrors();
    }

    @Then("I verify the highlighted errors on {string} date field on {string} row")
    public void verifyHighLightedError(String date, String row) {
        RebatesController controller = new RebatesController();
        controller.verifyItemDateToolTip(date, row);
        JLog.write("Successfully verified the tooltip on date columns");
        checkForErrors();
    }

    @Then("I verify the results under View Results tab")
    public void verifyResultsUnderViewResults() throws Throwable {
        RebatesController controller = new RebatesController();
        boolean status = controller.verifyViewResults();
        Verify.verify(status, "Unable to verify results under View Results.");
        JLog.write("Successfully verified the results under View Results.");
        checkForErrors();
    }

    @And("I mousehover on {string} date field to verify {string}")
    public void setStartDate(String date, String expTxt) throws ParseException {
        RebatesController controller = new RebatesController();
        controller.mouseHoverAndVerifyText(date, expTxt);
        JLog.write("Successfully mousehovered on " + date + " date field and verified the tooptip -> " + expTxt
                + " is displayed.");
        checkForErrors();
    }

    @And("I click on the rulesApplied link")
    public void clickRulesApplied() throws ParseException {
        RebatesController controller = new RebatesController();
        controller.clickRulesAppliedLink();
        JLog.write("Clicked on Rules Applied Link.");
        checkForErrors();
    }

    @And("I should be landing on rules tab")
    public void verifyPricingTab() throws ParseException {
        RebatesController controller = new RebatesController();
        controller.isLandedonRulesTab();
        JLog.write("Verified that webpage landed is under Rules Tab.");
        checkForErrors();
    }

    @Then("I verify Platform Name list is sorted on alphabetical order")
    public void verifyListSorted() {
        RebatesController.setOverrideContext("contentFrame", "mainModalFrame");
        RebatesController controller = new RebatesController();
        boolean status = controller.getPlatFormNamesAndVerifySorted();
        Verify.verify(status, "Platform Name list is not in alphabetical order.");
        JLog.write("Platform Name list is in alphabetical order.");
    }

    @And("I verify Platform Type column has values either AGILE or PROTEUS displayed for all rows on popup")
    public void isCoulmnValueVisibleOnPopup() throws ParseException {
        RebatesController.setOverrideContext("contentFrame", "mainModalFrame");
        RebatesController controller = new RebatesController();
        controller.isCoulmnValuesDisplayedOnPopup();
        JLog.write("Successfully verified column name Platform has values AGILE or PROTEUS");
        checkForErrors();
    }

    @And("I click on the Save Rule button")
    public void clickSaveButton() {
        RebatesController controller = new RebatesController();
        controller.clickSaveRuleBtn();
        checkForErrors();
    }

    @And("I click on the {string} Button on Rules Tab")
    public void clickButton(String btnName) {
        RebatesController controller = new RebatesController();
        controller.clickBtn(btnName);
        JLog.write("Clicked on " + btnName);
        checkForErrors();
    }

}

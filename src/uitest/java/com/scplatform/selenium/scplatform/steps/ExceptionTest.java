/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.joda.time.DateTime;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.ExceptionController;
import com.test.selenium.scplatform.ui.main.download.SCPlatformDownloadController;
import com.google.common.base.Verify;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExceptionTest {

    static String timeStamp = DateTime.now().toString("yyMMddHHmmss");
    ExceptionController controller;
    Prop prop = Prop.getInstance();

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

    @Then("I verify the new exception details displayed with {string} status")
    public void verifyFieldsOnNewExcep(String status) throws IOException {
        controller = new ExceptionController();
        controller.verifyNewExcepDetails(status);
        JLog.write("Verified New Exception details created.");
    }

    @And("I enter {string} on CostType autoComplete Field")
    public void enterTextFieldElementValue(String val) {
        JLog.write("Getting timestamp from upload controller");
        controller = new ExceptionController();
        controller.setCostTypeEleWithValue(val);
        JLog.screenCapture();
        checkForErrors();
    }

    @Then("I remove the attached CR file")
    public void removeAttachedCR() throws IOException {
        AbstractPage.sleep(1);
        controller = new ExceptionController();
        controller.clickCloseBtnAfterAttachment();
        JLog.write("Removed attached CR successfully");
    }

    @Then("I verify exception pricing details")
    public void verifyExcepPricingDetailsWithATtachments() throws IOException {
        controller = new ExceptionController();
        controller.verifyExcepPricingDetails();
        JLog.write("Verified Exception Pricing details with attachment.");
    }

    @Then("I verify close button is visible after CR attachment")
    public void verifyCloseBtnAfterAttachment() throws IOException {
        controller = new ExceptionController();
        controller.verifyCloseBtnAfterAttachment();
    }

    @Then("I verify close button is not visible after CR attachment")
    public void verifyCloseBtnNotVisibleAfterAttachment() throws IOException {
        controller = new ExceptionController();
        controller.verifyCloseBtnNotVisibleAfterAttachment();
    }

    @Then("I verify error msg {string} displayed on approver field")
    public void verifyExcepApproverErrorMsg(String msg) throws IOException {
        controller = new ExceptionController();
        controller.verifyExcepApproverErrorValidation(msg);
        JLog.write("Verified Error msg displayed on Approver field.");
        checkForErrors();
    }

    @Then("I verify the exception status as {string} on exception edit page")
    public void verifyExcepPricingStatus(String status) throws IOException {
        controller = new ExceptionController();
        controller.excepStatus(status);
        JLog.write("Verified Exception Pricing Status on edit excep page.");
    }

    @Then("I save the exceptionName and exceptionID for verification")
    public void saveExcepNameID() throws IOException {
        controller = new ExceptionController();
        controller.saveNewExcepNameID();
    }

    @Then("I verify the validaion error msg {string}")
    @Then("I verify the validation error msg {string}")
    @Then("I verify the validattion error msg {string}")
    public void verifyValidationPopup(String msg) throws IOException {
        controller = new ExceptionController();
        controller.verifyValidationErrorMsg(msg);
        JLog.write("Successfully verified validation error.");
    }

    @Then("I verify no validaion error msg displayed")
    @Then("I verify no validation error msg displayed")
    public void verifyNoValidationPopup() throws IOException {
        controller = new ExceptionController();
        controller.verifyNoValidationErrorMsg();
        JLog.write("Successfully verified that no validation error displayed.");
    }

    @Then("I verify the exceptionName and exceptionID after edit action")
    public void verifyExcepNameID() throws IOException {
        controller = new ExceptionController();
        controller.verifyEditExcepNameID();
    }

    @When("I enter reject Comments")
    public void enterRejComments() throws FileNotFoundException {
        controller = new ExceptionController();
        controller.setElementValue("actionComments", "Testing reject action");
        JLog.write("Set reject comments");
    }

    @When("I enter approve comments as {string}")
    public void enterApproveComments(String txt) throws FileNotFoundException {
        controller = new ExceptionController();
        controller.setElementValue("actionComments", txt);
        JLog.write("Set Approve comments");
    }

    @Then("I search by name {string} and verify details with {string} status on Search Exception Page")
    public void searchNVerifyFieldsOnSearchExcep(String name, String status) throws Throwable {
        controller = new ExceptionController();
        controller.searchAndverifyNewExcepDetails(name, status);
        JLog.write("Verified Exception details on search Exception Page.");
    }

    @Then("I search by ExceptionID generated to verify the new exception details on Search Exception Page")
    public void searchNVerifyFieldsOnSearchExcepWithID() throws Throwable {
        controller = new ExceptionController();
        controller.searchAndverifyNewExcepDetailsWithID();
        JLog.write("Verified New Exception details on search Exception Page.");
    }

    @Then("I search by ExceptionID of autoExcep to verify the new exception details on Search Exception Page")
    public void searchAndverifyAutoExcepDetailsWithID() throws Throwable {
        controller = new ExceptionController();
        controller.searchAndverifyAutoExcepDetailsWithID();
        JLog.write("Verified New Exception details on search Exception Page.");
    }

    @Then("I download and verify the Pre-Requisite Rule, Fiscal Calender & ODM POC Link")
    public void downloadNVerifyLink() throws Throwable {
        // controller = new ExceptionController();
        // controller.clickPreRequisiteLink();
        SCPlatformDownloadController dc = new SCPlatformDownloadController();
        String dwnloadedFile = dc.getPreRequisiteLink();
        Verify.verify(!dwnloadedFile.equals(""), "Unable to find downloaded file.");
        // as of now no content it is an empty file, so jus validating
        // filenName exists
    }

    @Then("I download and verify {int} ODM email file")
    public void downloadNVerifyODMLink(int count) throws Throwable {
        controller = new ExceptionController();
        Verify.verify(controller.getDownloadLinkCount() == count,
                "Mismatch on no of files attached that is availablef ro download under odm emails");
        JLog.write("Successfully verified no of files attached that is available for download");
        if (count < 3) {
            SCPlatformDownloadController dc = new SCPlatformDownloadController();
            dc.verifyODMEmailLink(count);
        }
    }

    @Then("I should not see delete button for odm emails")
    public void verifyBtnNotDisplayedAndEnabled() {
        controller = new ExceptionController();
        boolean state = controller.verifyDelBtn();
        Verify.verify(!state, "Delete icon is displayed and enabled.");
        JLog.write("Verified that delete icon is not displayed and is disabled.");
    }

    @When("I select the exception state as {string}")
    public void selectExceptionStateValue(String state) throws Throwable {
        controller = new ExceptionController();
        controller.selectExceptionStateValue(state);
        JLog.write("Successfully set exception state value.");
    }

    @Then("I verify exception results are displayed with state as {string}")
    public void verifyExcepStateUnderResults(String status) throws Throwable {
        controller = new ExceptionController();
        controller.verifyExcepResultsStatus(status);
        checkForErrors();
    }

    @Then("I verify new exception is created with a different exceptionID")
    public void verifyDiffExcepID() throws Throwable {
        controller = new ExceptionController();
        controller.searchAndverifyNewExcepDetailsWithID();
        JLog.write("Verified New Exception details on search Exception Page.");
    }

    @Then("I verify data entered on fields")
    public void verifyData() throws Throwable {
        controller = new ExceptionController();
        // controller.verifyNewExcepDetailsEnteredOnFields();

        InputStream fo = new FileInputStream(prop.getRootDir() + "scplatform/data/properties/exceptionDetails.properties");
        Properties p = new Properties();
        p.load(fo);
        Verify.verify(controller.getTextFieldValueWithName("exceptionName").contains("Excep"),
                "Excep Name field is cleared");
        Verify.verify(controller.getTextFieldValueWithName("platformName").equals("testPlatform"),
                "Platform field is cleared");
        Verify.verify(controller.getTextFieldValueWithName("exceptionOwner").equals(p.getProperty("owner")),
                "owner field is cleared");
        Verify.verify(controller.getTextFieldValueWithName("exceptionApprover").contains(p.getProperty("approver")),
                "approver field is cleared");
        JLog.write("Verified that data entered on fields are stil persisting.");
    }

    @When("I enter {string} on the exceptionOwner textfield")
    public void enterExcepOwner(String name) throws Throwable {
        controller = new ExceptionController();
        controller.setExceptionOwner(name);
        JLog.write("Successfully set exception owner details.");
    }

    @When("I enter {string} on the exceptionApprover textfield")
    public void enterExcepApprover(String name) throws Throwable {
        controller = new ExceptionController();
        controller.setExceptionApprover(name);
        JLog.write("Successfully set exception Approver details.");
    }

    @When("I enter {string} on the platformName textfield")
    public void enterExcepPlatformName(String name) throws Throwable {
        controller = new ExceptionController();
        controller.setExceptionPlatform(name);
        JLog.write("Successfully set exception PlatformName details.");
    }

    @When("I select {string} on Request Type Combobox")
    public void enterExcepReqType(String name) throws Throwable {
        controller = new ExceptionController();
        controller.setExceptionRequestType("Request Type", name);
        JLog.write("Successfully set exception Request Type details.");
    }

    @Then("I verify Requestor-Approver details with {string} status displayed")
    public void verifyReqAppDetails(String status) throws Throwable {
        controller = new ExceptionController();
        controller.verifyReqAppDetails(status);
        JLog.write("Successfully verified details of requestor approver with status.");
    }

    @And("I delete {string} ODM email files attached")
    public void delODMFile(String noOfFiles) throws Throwable {
        controller = new ExceptionController();
        int count = Integer.parseInt(noOfFiles);
        controller.deleteFiles(count);
        JLog.write("Successfully deleted " + count + " file.");
    }

    @And("I verify {string} files are displayed after delete action")
    public void verifyODMFile(String noOfFiles) throws Throwable {
        controller = new ExceptionController();
        int count = Integer.parseInt(noOfFiles);
        controller.verifyDeletedFiles(count);
        JLog.write("Successfully verified deleted files count.");
    }

    @When("I select {string} on Cost Type Combobox")
    public void enterCostType(String name) throws Throwable {
        controller = new ExceptionController();
        controller.setExceptionCostType("Cost Type", name);
        JLog.write("Successfully set exception Cost Type details.");
    }

}

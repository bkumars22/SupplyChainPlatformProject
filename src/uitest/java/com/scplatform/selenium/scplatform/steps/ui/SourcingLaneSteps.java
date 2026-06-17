/*
 * SourcingLaneSteps.java
 * Created on Mar 19, 2021
 *
 * Copyright (c) 2021 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.steps.ui;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage.ResultsFound;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.NewSourcingLaneController;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.NewSourcingLaneModel;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.NewSourcingLaneResultsController;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.NewSourcingLaneResultsPage;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.details.SourcingLaneInformationController;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.details.SourcingLaneInformationModel;
import com.test.selenium.scplatform.ui.costing.searchSourcingLane.SearchSourcingLaneController;
import com.test.selenium.scplatform.ui.costing.searchSourcingLane.SearchSourcingLaneModel;
import com.test.selenium.scplatform.ui.costing.searchSourcingLane.SearchSourcingLaneResultsController;
import com.test.selenium.scplatform.ui.costing.searchSourcingLane.SearchSourcingLaneResultsPage;
import com.test.selenium.scplatform.utilities.MessageIO;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class SourcingLaneSteps {
  protected SCPlatformNavigation nav;

  @Before
  public void beforeMethod(Scenario scenario){
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
    nav = new SCPlatformNavigation();
  }

  private void checkForErrors()	{
    if (JLog.getErrorCount() > 0){
      JLog.fail(JLog.getErrorCount() + " errors occured in the test.  Check log.", TakeScreenshot.True);
    }
  }


  /**
   * http://confluence.dev.scplatform.local/display/QA/Search+Sourcing+Lane
   */
  @Given("I validate the {string} Sourcing Lane Data and {string} Item data")
  public void validSourcingLane(String sourcingLaneSaveKey, String itemSaveKey){
    MessageIO<SourcingLane> messageIOSourcingLane = new MessageIO<>(SourcingLane.class);
    MessageIO<Item> messageIOItem = new MessageIO<>(Item.class);

    nav.SearchSourcingLane();
    SearchSourcingLaneResultsController c = new SearchSourcingLaneResultsController();
    c.validate(
        messageIOSourcingLane.load(sourcingLaneSaveKey),
        messageIOItem.load(itemSaveKey));

    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   * @param model
   */
  @And("I search New Sourcing Lane with values")
  public void searchNewSourcingLane(List<NewSourcingLaneModel> model){
    nav.NewSourcingLane();

    List<NewSourcingLaneModel> processedModel = Preprocessing.process(model);
    NewSourcingLaneModel newSourcingLaneModel = processedModel.get(0);

    NewSourcingLaneController newSourcingLaneController = new NewSourcingLaneController();
    newSourcingLaneController.setModel(newSourcingLaneModel);
    newSourcingLaneController.search();

    if (StringUtils.isNotBlank(newSourcingLaneModel.getItemNumber()))	{
      NewSourcingLaneResultsController newSourcingLaneResultsController = new NewSourcingLaneResultsController();
      newSourcingLaneResultsController.select(newSourcingLaneModel.getItemNumber());
    }
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   * @param model
   */
  @And("I Search Sourcing Lane with values")
  public void searchSourcingLane(List<SearchSourcingLaneModel> model){
    nav.SearchSourcingLane();

    List<SearchSourcingLaneModel> processedModel = Preprocessing.process(model);
    SearchSourcingLaneModel searchSourcingLaneModel = processedModel.get(0);

    SearchSourcingLaneController searchSourcingLaneController = new SearchSourcingLaneController();
    searchSourcingLaneController.setModel(searchSourcingLaneModel);
    searchSourcingLaneController.search();

    if (StringUtils.isNotBlank(searchSourcingLaneModel.getItemNumber()))	{
      SearchSourcingLaneResultsController searchSourcingLaneResultsController = new SearchSourcingLaneResultsController();
      searchSourcingLaneResultsController.select(searchSourcingLaneModel.getItemNumber());
    }
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   * @param model
   */
  @Then("I set Sourcing Lane Information with values")
  public void setSourcingLaneInformation(List<SourcingLaneInformationModel> model) throws Exception{
    List<SourcingLaneInformationModel> processedModel = Preprocessing.process(model);
    SourcingLaneInformationModel sourcingLaneInformationModel = processedModel.get(0);

    SourcingLaneInformationController c = new SourcingLaneInformationController();
    c.setModel(sourcingLaneInformationModel);
    c.create();
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @Then("I {string} the Sourcing Lane Information")
  public void sourcingLaneInformationAction(String action){
    SourcingLaneInformationController c = new SourcingLaneInformationController();
    c.clickAction(action);
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @And("I save the Sourcing Lane Information Lane Name as {string}")
  public void saveSourcingLaneInformationLaneName(String saveLaneNameKey)	{
    SourcingLaneInformationController c = new SourcingLaneInformationController();
    String laneName = c.getLaneName();
    JLog.write("Sourcing Lane Information Lane Name: " + laneName);

    Configuration.setRuntime(saveLaneNameKey, laneName);
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @Then("I verify Sourcing Lane Information Status is {string}")
  public void verifySourcingLaneInformationStatus(String expectedStatus){
    SourcingLaneInformationController c = new SourcingLaneInformationController();
    c.verifyStatus(expectedStatus);
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @Then("I verify New Sourcing Lane results found is greater than \"{int}\"")
  public void verifyNewSourcingLaneResultsCount(int count)	{
    NewSourcingLaneResultsPage newSourcingLaneResultsPage = new NewSourcingLaneResultsPage();
    ResultsFound resultsFound = newSourcingLaneResultsPage.getResultsFoundData();
    verifyGreaterThan(count, resultsFound);
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @Then("I verify Search Sourcing Lane results found is greater than \"{int}\"")
  public void verifySourcingLaneResultsCount(int count)	{
    SearchSourcingLaneResultsPage searchSourcingLaneResultsPage = new SearchSourcingLaneResultsPage();
    ResultsFound resultsFound = searchSourcingLaneResultsPage.getResultsFoundData();
    verifyGreaterThan(count, resultsFound);
    checkForErrors();
  }


  /**
   * TODO: DOCUMENT
   */
  @Then("I verify New Sourcing Lane results found is equal to \"{int}\"")
  public void verifyNewSourcingLaneResultsEquals(int count)	{
    NewSourcingLaneResultsPage newSourcingLaneResultsPage = new NewSourcingLaneResultsPage();
    ResultsFound resultsFound = newSourcingLaneResultsPage.getResultsFoundData();
    verifyEquals(count, resultsFound);
    checkForErrors();
  }

  /**
   * TODO: DOCUMENT
   */
  @Then("I verify Search Sourcing Lane results found is equal to \"{int}\"")
  public void verifySourcingLaneResultsEquals(int count)	{
    SearchSourcingLaneResultsPage searchSourcingLaneResultsPage = new SearchSourcingLaneResultsPage();
    ResultsFound resultsFound = searchSourcingLaneResultsPage.getResultsFoundData();
    verifyEquals(count, resultsFound);
    checkForErrors();
  }



  private void verifyGreaterThan(int count, ResultsFound resultsFound)	{
    if (resultsFound.getResultsFoundCount() > count)	{
      JLog.write(String.format("Verify more than %d records where found.  Success (%s)", count, resultsFound.getResultsFound()));
    } else	{
      JLog.error(
          String.format(
              "Verify more than %d records where found.  Failed (%s).  There where %d results found.",
              count,
              resultsFound.getResultsFound(),
              resultsFound.getResultsFoundCount()),
          TakeScreenshot.True);
    }
  }

  private void verifyEquals(int count, ResultsFound resultsFound)	{
    if (resultsFound.getResultsFoundCount() == count)	{
      JLog.write(String.format("Verify %d records where found.  Success (%s)", count, resultsFound.getResultsFound()));
    } else	{
      JLog.error(
          String.format(
              "Verify %d records where found.  Failed (%s).  There where %d results found.",
              count,
              resultsFound.getResultsFound(),
              resultsFound.getResultsFoundCount()),
          TakeScreenshot.True);
    }
  }
}

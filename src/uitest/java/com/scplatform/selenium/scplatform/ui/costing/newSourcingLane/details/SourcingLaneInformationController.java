/*
 * SourcingLaneInformationController.java
 * Created on Oct 3, 2019
 *
 * Copyright (c) 2019 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.ui.costing.newSourcingLane.details;

import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.common.unity.actions.Button;
import com.test.selenium.scplatform.modelViewController.SCPlatformController;

public class SourcingLaneInformationController extends SCPlatformController {
  SourcingLaneInformationPage page;

  @Override
  public void create() throws Exception {
    super.assertModelsExist();
    for (Model model : models) {
      populateValues(model);
    }
  }

  public void print()	{
    SourcingLaneInformationModel model = page.parse();
    model.print();
  }

  public SourcingLaneInformationController()	{
    page = new SourcingLaneInformationPage();
  }

  @Override
  public PageImpl getView() {
    return new SourcingLaneInformationPage();
  }

  public void clickRefresh()	{
    clickAndCheckForPOSTError(page.refreshButton());
  }

  public void clickClose()	{
    clickAndCheckForPOSTError(page.closeButton());
  }

  public void clickApprove()	{
    clickAndCheckForPOSTError(page.approveButton());
  }

  public void clickSubmit()	{
    clickAndCheckForPOSTError(page.submitButton());
  }

  public void clickReopen()	{
    clickAndCheckForPOSTError(page.reopenButton());
  }

  public void clickAction(String action){
    if (action.equalsIgnoreCase("Refresh"))	{
      clickRefresh();
    } else if (action.equalsIgnoreCase("Close"))	{
      clickClose();
    } else if (action.equalsIgnoreCase("Approve"))	{
      clickApprove();
    } else if (action.equalsIgnoreCase("Submit"))	{
      clickSubmit();
    } else if (action.equalsIgnoreCase("Reopen"))	{
      clickReopen();
    } else	{
      Button.clickButton(action);
    }
  }

  public String getLaneName()	{
    return page.getElementValue(page.laneName()).toString();
  }

  public boolean verifyStatus(String expectedStatus){
    SourcingLaneInformationModel actual = new SourcingLaneInformationModel();
    String actualStatus = page.getElementValue(page.status()).toString();
    return verify(actual.getDisplayName("status"), actualStatus, expectedStatus);
  }
}

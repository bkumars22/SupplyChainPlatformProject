/*
 * MessageManipulation.java
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

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.utilities.MessageIO;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class MessageManipulation {

  @Before
  public void beforeMethod(Scenario scenario){
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
  }

  private void checkForErrors()	{
    if (JLog.getErrorCount() > 0){
      JLog.fail(JLog.getErrorCount() + " errors occured in the test.  Check log.", TakeScreenshot.True);
    }
  }


  @Given("I spit {string} SupplierAllocation Message into \"{int}\" parts")
  public void splitSupplierAllocation(String supplierAllocationSaveKey, int numberOfParts){
    MessageIO<SupplierAllocation> messageIO = new MessageIO<>(SupplierAllocation.class);
    List<SupplierAllocation> supplierAllocationData = messageIO.load(supplierAllocationSaveKey);

    List<String> itemUniqueIdList = new ArrayList<>();
    for (SupplierAllocation data : supplierAllocationData)	{
      if (!itemUniqueIdList.contains(data.getItemUniqueId()))	{
        itemUniqueIdList.add(data.getItemUniqueId());
      }
    }


    int startLine = 0;
    int maxLines = itemUniqueIdList.size() / numberOfParts;
    int endLine = maxLines;

    for (int splitParts = 0; splitParts < numberOfParts; splitParts++){
      List<SupplierAllocation> newAllocationList = new ArrayList<>();

      for (int line = startLine; line < endLine; line++){
        for (SupplierAllocation data : supplierAllocationData)	{
          if (itemUniqueIdList.get(line).equals(data.getItemUniqueId())){
            newAllocationList.add(data);
          }
        }
      }


      startLine = endLine;
      if ((splitParts+1) == (numberOfParts-1)){
        endLine = itemUniqueIdList.size();
      } else	{
        endLine = startLine + maxLines;
      }

      messageIO.save(newAllocationList, supplierAllocationSaveKey + (splitParts+1));
    }

    checkForErrors();
  }
}

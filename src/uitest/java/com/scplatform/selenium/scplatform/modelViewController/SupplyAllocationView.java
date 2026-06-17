/*
 * SupplyAllocationView.java
 * Created on Mar 26, 2021
 *
 * Copyright (c) 2021 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.filedownloader.ActionsForDownload;

public class SupplyAllocationView extends MTCMView {

  @Override
  public void setContext() {
    if (getOverrideContext() == null) {
      String[] frames = new String[1];
      frames[0] = "iframe[id='contentFrame']";
      setFrame(frames);
    } else {
      String[] frames = { "iframe[name='contentFrame']", "iframe[id='mainModalFrame']" };
      setFrame(frames);
    }
  }

  public WebElement getTAMCheckBox(String val) {
    return get(By.xpath("//input[@name='accessRights(TAM)' and contains(@value,'" + val + "')]"));
  }

  public WebElement getFGNameLabel() {
    return get(By.xpath("//div[@data-tooltip='#header-tooltip-fg']//b"));
  }

  public WebElement getAllocLabel(String i) {
    return get(By.xpath("//div[@id='expandRow']//div[" + i + "]//b"));
  }

  public WebElement getLevelLabel(String i) {
    return get(By.xpath("//div[@id='expandRow']//div[" + i + "]//span"));
  }

  // public WebElement getRegionAllocLabel() {
  // return get(By.xpath("//div[@id='expandRow']//div[3]//b"));
  // }
  //
  // public WebElement getRegionLevelLabel() {
  // return get(By.xpath("//div[@id='expandRow']//div[3]//span"));
  // }
  //
  // public WebElement getSiteAllocLabel() {
  // return get(By.xpath("//div[@id='expandRow']//div[3]//b"));
  // }
  //
  // public WebElement getSiteLevelLabel() {
  // return get(By.xpath("//div[@id='expandRow']//div[3]//span"));
  // }

  public List<WebElement> getResultsMRPSite() {
    return getList(By.xpath("//tr//td[4]"));
  }

  public WebElement getSuppAllocTotal() {
    return get(By.xpath("//td[contains(@class,'totalCal')][3]"));
  }

  public WebElement getAllocOption(String option) {
    return get(By.xpath("//input[@id='" + option + "']"));
  }

  public WebElement getAllocDeleteBtn() {
    return get(By.xpath("//button[contains(@onclick,'javascript:deleteSelected()')]"));
  }

  public WebElement getAllocDeleteBtnOnSite() {
    return get(By.xpath("//button[contains(@onclick,'javascript:deleteSite()')]"));
  }

  public WebElement getRecordsCountLabel() {
    return get(By.xpath(
        "//div[contains(text(),'Supplier Allocation result found') and contains(text(),'Item Allocation result found')]"));
  }

  public List<WebElement> getItemAllocationColumns() {
    return getList(
        // By.xpath("//td//div[@class='compact']//input[@class='itemAllocationBox
        // eto-input__field compact']"));
        By.xpath(
            "//td//div[@class='compact' or @class='supplierGroup compact']//input[@class='itemAllocationBox eto-input__field compact']"));
  }

  public WebElement getCalendarDay(String day) {
    // return
    // get(By.xpath("//tr[@class='daysrow']//td[contains(@class,'day') and
    // text()='" + day + "']"));
    return get(By.xpath(
        "//tr[@class='daysrow']//td[contains(@class,'day') and not(contains(@class,'day othermonth')) and text()='"
            + day + "']"));
  }

  public List<WebElement> getItemAllocRowWise(String row) {
    // return
    // getList(By.xpath("//tr[contains(@class,'hideItemAllocation')][" + row
    // + "]//td//div[@class='compact' or @class=' supplierGroup
    // compact']//input[@class='itemAllocationBox eto-input__field
    // compact']"));
    List<WebElement> elements = getList(By.xpath("//tr[" + row
        + "]//div[not(contains(@class,'pastBucketBoxColor')) and contains(@class,'compact')]//input[@class='itemAllocationBox eto-input__field compact']"));
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = getList(By.xpath("//tr[" + row
          + "]//div[not(contains(@class,'pastBucketBoxColor')) and contains(@class,'compact')]//input[@class='itemAllocationBox eto-input__field compact']"));
    }
    return elements;
  }

  public List<WebElement> getSuppAllocRowWise(String row) {
    List<WebElement> elements = getList(By.xpath("//tr[" + row
        + "]//div[@class='compact' or @class='supplierGroup compact']//input[@class='eto-input__field supplier_allocation compact']"));
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = getList(By.xpath("//tr[" + row
          + "]//div[@class='compact' or @class='supplierGroup compact']//input[@class='eto-input__field supplier_allocation compact']"));
      ;
    }
    return elements;
    // tr[2]//div[@class='compact' or @class='supplierGroup
    // compact']//input[@class='eto-input__field supplier_allocation
    // compact']
  }

  public List<WebElement> getSupplyAllocationColumns() {
    return getList(
        By.xpath(
            "//div[@class='compact']//input[@class='eto-input__field supplier_allocation compact']"));
  }

  public List<WebElement> getMultipleSupplyAllocationColumns() {
    List<WebElement> results = null;
    if (exists(By.xpath(
        "//div[@class='supplierGroup compact']//input[@class='eto-input__field supplier_allocation compact']")))
      results = getList(By.xpath(
          "//div[@class='supplierGroup compact']//input[@class='eto-input__field supplier_allocation compact']"));
    return results;
  }

  public List<WebElement> getInheritChecBoxes(String name) {
    // return getList(By.xpath("//input[@name='" + name + "' and @type
    // ='checkbox']"));
    return getList(By
        .xpath("//th[not(contains(@class,'pastBucket'))]//input[@name='" + name + "' and @type ='checkbox']"));
  }

  public List<WebElement> getSuppAllocateToAll() {
    // if(exists(By.xpath("//th[@class=' pastBucketBoxColor compact']")))
    // return getList(
    // By.xpath("//th[@class=' pastBucketBoxColor
    // compact']/following-sibling::th[@class='compact']//i"));
    return getList(By.xpath("//i[contains(@onclick,'copySupplierAllocationsForward')]"));
  }

  public List<WebElement> getItemAllocateToAll() {
    // if(exists(By.xpath("//th[@class='pastBucketWhiteBorder
    // pastBucketBoxColor compact']")))
    // return getList(
    // By.xpath("//th[@class='pastBucketWhiteBorder pastBucketBoxColor
    // compact']/following-sibling::th//i"));
    return getList(By.xpath("//i[contains(@onclick,'copyItemAllocationsForward')]"));
  }

  public WebElement getCurrentPastField(String val) {
    return get(By.xpath("//span[text()='" + val + "']"));
  }

  public List<WebElement> getDropDownForAlloc() {
    // if(exists(By.xpath(
    // "//th[@class='pastBucketBoxColor mrpCol compact']")))
    // return getList(By.xpath(
    // "//th[@class='pastBucketBoxColor mrpCol
    // compact'][2]/following-sibling::th[@class='compact']//span[@class='eto-dropdown']"));
    return getList(By.xpath("//th[@class='compact']//span[@class='eto-dropdown']"));
  }

  public List<WebElement> getDropDownOptionsForAlloc(String allocTo, String option) {
    return getList(
        By.xpath("//a[contains(@href,'" + allocTo + "Allocations') and contains(text(),'" + option + "')]"));
  }

  public WebElement regionTab(String tabName) {
    return get(By.xpath("//span[text()='" + tabName + "']"));
  }

  public WebElement getCloseBtnOnDelete() {
    return get(By.xpath(
        "//button[text()='Delete' and @onclick='javascript:deleteSelected()']/preceding-sibling::button[text()='Close']"));
  }

  public List<WebElement> getMultipleSuppAlloc(String r) {
    return getList(By.xpath("//tr[" + r
        + "]//td//div[@class='compact' or @class='supplierGroup compact']//input[@class='eto-input__field supplier_allocation compact']"));
    // return
    // getList(By.xpath("//tr[contains(@class,'hideItemAllocation')][" + r
    // + "]//td[@class='eto-grid-edit-cell
    // compact']//input[@class='itemAllocationBox eto-input__field compact'
    // and contains(@name,'itemAllocation')]"));
  }

  public List<WebElement> getMultipleItemAlloc(String r) {
    return getList(By.xpath("//tr[contains(@class,'hideItemAllocation')][" + r
        + "]//td[@class='eto-grid-edit-cell compact']//input[@class='itemAllocationBox eto-input__field compact' and contains(@name,'itemAllocation')]"));
  }

  public ActionsForDownload actionsForDownload(String downloadOption) {
    // resetContextSet();
    // setFrameContext();

    return new ActionsForDownload() {

      @Override
      public boolean execute() {
        boolean success = true;

        try {
          // List<WebElement> buttons = getList(By.tagName("button"));
          // for (WebElement button : buttons) {
          // System.out.println(String.format("id='%s'; title='%s'",
          // button.getAttribute("id"),
          // button.getAttribute("title")));
          // // }
          // WebElement downloadButton =
          // get(By.id("downloadButtonId"));
          //
          // Actions actions = new Actions(browser());
          // actions.moveToElement(downloadButton).
          // click(get(By.xpath(String.format("//a[contains(@onclick,
          // 'tamDownlaodOption('%s')')]", downloadOption))))
          // .build().perform();

          WebElement ele = getIconBtn("file_download");
          String mouseOverScript = "if(document.createEvent){var evObj = document.createEvent('MouseEvents');evObj.initEvent('mouseover',true, false); arguments[0].dispatchEvent(evObj);} else if(document.createEventObject) { arguments[0].fireEvent('onmouseover');}";
          executeJavaScript(mouseOverScript, ele);
          sleep(1);
          ele = get(By.xpath("//a[contains(@onclick,'" + downloadOption + "')]"));
          executeJavaScript("arguments[0].click();", ele);
        } catch (Exception e) {
          success = false;
          JLog.error(null, e, TakeScreenshot.True);
        }

        return success;
      }
    };

  }

}
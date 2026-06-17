/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ExceptionView extends MTCMView {

  @Override
  public void setContext () {
    if (getOverrideContext() == null) {
      String[] frames = new String[1];
      frames[0] = "iframe[id='contentFrame']";
      setFrame(frames);
    }
  }

  public WebElement getCostType () {
    return getElement(By.xpath("//div[@id='complexSelectcostType']//input"));
  }

  public WebElement getStatusReqPanel () {
    return getElement(By.id("approvalDetailDiv"));
  }

  public WebElement getReqStatusApproverPanelLabels (String text) {
    return getElement(
        By.xpath("//div[@class='eto-gauge__label']//span[contains(text(),'" + text + "')]"));
  }

  public WebElement getReqStatusApproverPanelIndicatorLabels (String text) {
    return getElement(
        By.xpath("//div[@class='eto-gauge__indicator']//span[contains(text(),'" + text + "')]"));
  }

  public WebElement getExcepExpandMoreBtn () {
    // Use get() (returns null) instead of getElement() (throws) for safe handling
    WebElement btn = get(By.xpath("//div[@id='complexSelectexceptionState']//span[text()='expand_more']"));
    if (btn == null) {
      // Fallback: try aria-based or class-based selector for the expand button
      btn = get(By.xpath("//div[@id='complexSelectexceptionState']//span[contains(@class,'expand')]"));
    }
    if (btn == null) {
      // Fallback: try any clickable toggle button inside the complex select
      btn = get(By.xpath("//div[@id='complexSelectexceptionState']//button[contains(@class,'toggle') or contains(@class,'drop')]"));
    }
    return btn;
  }

  public List<WebElement> getExcepResultsStatus () {
    return getList(By.xpath("//tr//td[6]//div"));
  }

  public WebElement getAttachedFileDownloadIcon () {
    return get(By.xpath("//table[@id='file-upload-details']//tr[1]//td[1]//i"));
  }

  public WebElement getAttachedFile () {
    return get(By.xpath("//table[@id='file-upload-details']//tr[1]//td[2]//span"));
  }

  public WebElement getAttachedFileDetails (String col) {
    return get(By.xpath("//table[@id='file-upload-details']//tr[1]//td[" + col + "]"));
  }

  public WebElement getExceptionStateTextField () {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectexceptionState']//label[contains(text(),'Exception State')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getDetailsOnNewExcepCreationPage (String r, String c) {
    return get(
        By.xpath(
            "//div[contains(@class,'eto-well margin-bottom-sm')]/div[" + r + "]/div[" + c
                + "]//span//b"));
  }

  public WebElement getPreReqLink () {
    return get(By.xpath("//a[contains(@onclick,'callPreRequiSite();')]//b"));
  }

  public List<WebElement> getDeleteLinks () {
    return getList(By.xpath("//span[contains(@class,'md-icon remove eto-upload__remove-icon')]"));
  }

  public List<WebElement> getDownloadLinks () {
    return getList(By.xpath("//i[contains(@onclick,'downloadEmail')]"));
  }

  public List<WebElement> getDownloadLinksText () {
    return getList(
        By.xpath("//i[contains(@onclick,'downloadEmail')]/ancestor::td/following-sibling::td"));
  }

  public WebElement getTableValue (String r, String c) {
    return get(By.xpath("//div[@id='grid-result']//tr[" + r + "]//td[" + c + "]"));
  }

  public WebElement getExceptionId () {
    return get(By.xpath("//h1"));
  }
}

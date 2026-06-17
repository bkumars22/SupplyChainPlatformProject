/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.businessEntities;

//Author : Kumar

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.scplatform.modelViewController.MTCMView;

public class BusinessEntitiesView extends MTCMView {

//    @Override
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

    public List<WebElement> getNonEnterpriseRows() {
        return getList(By.xpath("//table//tr[td[contains(text(),'Supplier') or contains(text(),'Manufacturer')]]"));
    }

    public List<WebElement> getAllRows() {
        return getList(By.xpath("//table//tr"));
    }

  
    public WebElement getsearchBuinsesId() {
        return getElement(By.xpath("//input[@type='text' and @name='value(businessId)' and @id='searchField2']"));
    }

    public WebElement getsearchType() {
        return getElement(By.xpath("//input[contains(@class, 'eto-complex-combobox__field')]"));
    }

    public WebElement applyBtn() {
        return getElement(By.xpath(
                "//button[@type='button' and contains(@class, 'eto-btn--primary') and text()[normalize-space()='Apply']]"));

    }

    public WebElement getDataFileTypeDropdown() {
        return getElement(By.xpath("//select[@id='messageType']/option[@value='BusinessEntity']"));
    }

    public WebElement getBusinessEntityOption() {
        return getElement(By.xpath("//option[contains(text(),'Business Entity')]"));
    }

    public WebElement getFileInput() {
        return getElement(By.xpath("//input[@type='file']"));
    }

    public WebElement getUploadButton() {
        return getElement(By.xpath("//button[contains(text(),'Upload')]"));
    }

    public WebElement getSubmitButton() {
        return getElement(By.xpath("//button[contains(text(),'Submit')]"));    }
    
    
  

    public WebElement getSuccessMessage() {
        // Try a more generic locator for any visible message block after upload
        // Adjust the text as needed to match your actual UI
        return getElement(By.xpath(
                "//div[contains(@class,'eto-messageblock__body') and (contains(.,'uploaded') or contains(.,'success') or contains(.,'completed'))]"));
    }

    public WebElement getEntityRow(String entityName) {
        return getElement(By.xpath("//td[contains(text(),'" + entityName + "')]"));
    }

    /**
     * Logs all input fields on the page for debugging purposes.
     */
    public void logAllInputFields() {
        List<WebElement> inputs = browserSession.getDriver().findElements(By.tagName("input"));
        for (WebElement input : inputs) {
            System.out.println("Input: id=" + input.getDomAttribute("id") +
                    ", name=" + input.getDomAttribute("name") +
                    ", type=" + input.getDomAttribute("type") +
                    ", value=" + input.getDomAttribute("value"));
        }
    }
    
    public WebElement getItemDropdown() {
        return getElement(By.xpath("//select[@name='messageType(1)' and @id='messageType' and @class='inputField']"));
    }
    
    public WebElement getItemOption() {
        return getElement(By.xpath("//option[@value='ItemUI' and text()='Item(*.xlsx)']"));
    }
    
    public WebElement getItemAVLOption() {
        return getElement(By.xpath("//option[@value='ItemAVLUI' and normalize-space(text())='Item AVL(*.xlsx)']"));
    }
    
    public WebElement clickUploadHyperLink() {
        return getElement(By.xpath("//li[contains(text(),'uploaded and submitted to system as')]/a"));
    }
    
    public WebElement itemNumberTextField() {
        return getElement(By.xpath("//input[@id='searchField1' and @name='value(itemNumber)' and @type='text']"));
    }
    
    public WebElement selectRaidoBtn() {
        return getElement(By.xpath("//input[@type='radio' and @class='eto-radio__field eto-row-indicator' and @name='selectedPageKeys']"));
    }
    
    public WebElement selectSupplier() {
        return getElement(By.xpath("//div[contains(@class, 'eto-select__field-container')]/select[@id='supplierKey' and @name='supplierKey' and contains(@class, 'eto-select__field')]"));
    }
    
    public WebElement selectSourceSite() {
        return getElement(By.xpath("//div[contains(@class, 'eto-select__field-container')]/select[@id='fromSite' and @name='fromSiteKey' and contains(@class, 'eto-select__field')]"));
    }
  
    public WebElement sourceSubmitBtn() {
        return getElement(By.xpath("//button[@type='button' and @id='SubmitLaneEventButton' and contains(@class, 'eto-btn') and text()='Submit']"));
    }
    
    public WebElement sourceApprovedBtn() {
        return getElement(By.xpath("//button[@type='button' and @id='ApproveLaneEventButton' and contains(@class, 'eto-btn') and text()='Approve']"));
    }  
 
    
    public WebElement statusPendingCheck() {
        return getElement(By.xpath("//input[@type='text' and @class='eto-input__field' and @value='PENDING' and @disabled]"));
    }
    
    public WebElement statusApprovedCheck() {
        return getElement(By.xpath("//input[@type='text' and @class='eto-input__field' and @value='APPROVED' and @disabled]"));
    }
  
   
  
  

}

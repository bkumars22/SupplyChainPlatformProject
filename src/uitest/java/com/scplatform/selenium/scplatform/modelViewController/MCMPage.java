/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.modelViewController.view.PageImpl;

public class SCPlatformPage extends PageImpl {

    public void setContext() {
        String[] frames = new String[1];
        frames[0] = "iframe[name='contentFrame']";
        setFrame(frames);
    }

    public String[] homeMenu() {
        return null;
    }
    
	@Override
	public WebElement saveButton() {
		return getElement(By.partialLinkText("Save"));
	}

	public WebElement saveAndExitButton() {
		return getElement(By.partialLinkText("Save and Exit"));
	}
	
	@Override
	public WebElement editButton() {
		return getElement(By.partialLinkText("Edit"));
	}

	public WebElement backButton() {
		return getElement(By.partialLinkText("Back"));
	}
	
	@Override
	public WebElement cancelButton() {
		return getElement(By.partialLinkText("Cancel"));
	}

	@Override
	public WebElement addButton() {
		return getElement(By.partialLinkText("Add"));
	}

	@Override
	public WebElement searchButton() {
		 return getElement(By.partialLinkText("Search"));
	}
	
	public WebElement clearButton() {
		 return getElement(By.partialLinkText("Clear"));
	}

	@Override
    public String getErrorMessage() {
		setContext();
        StringBuilder msg = new StringBuilder();

        By elementLocator = By.xpath("//div[contains(@class, 'errorMessage')]");
        
        if (exists(elementLocator)) {
           WebElement element = browser().findElement(elementLocator);
           List<WebElement> errorText = element.findElements(By.tagName("li"));
           
           msg.append("An error has occurred\n");
           for (int i = 0; i < errorText.size(); i++){
        	   msg.append(errorText.get(i).getText());
        	   msg.append("\n");
           }
           
           elementLocator = By.partialLinkText("Show Details");
           if (exists(elementLocator)) {
        	   element = browser().findElement(elementLocator);
        	   element.click();
        	   
        	   elementLocator = By.id("detailArea");
        	   if (exists(elementLocator)) {
        		   element = browser().findElement(elementLocator);
        		   errorText = element.findElements(By.tagName("li"));
        		   
        		   msg.append("\nTechnical Details:\n");
                   for (int i = 0; i < errorText.size(); i++){
                	   msg.append(errorText.get(i).getText());
                	   msg.append("\n");
                   }
                   
                   elementLocator = By.className("instructionsArea");
               	   if (exists(elementLocator)) {
            		   element = browser().findElement(elementLocator);
            		   element = element.findElement(By.tagName("table"));
            		   errorText = element.findElements(By.tagName("tr"));
            		   
            		   msg.append("\nTrace Information:\n");
                       for (int i = 0; i < errorText.size(); i++){
                    	   msg.append(errorText.get(i).getText());
                    	   msg.append("\n");
                       }
            		   
               	   }
                   
        	   }
           }
           
           
        }


        if (StringUtils.isNotBlank(msg.toString())) {
        	String seperator = "\n===========================================================\n";
            return seperator + msg.toString() + seperator;
        } else {
            return null;
        }
    }

	public WebElement getLabelElement(String labelName){
		return getElement(By.xpath("//td[contains(text(),'Status')]"));
	}
	
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;

public class PriceTAMView extends MTCMView {

	@Override
	public void setContext() {
		if (getOverrideContext() == null) {
			String[] frames = new String[1];
			frames[0] = "iframe[id='contentFrame']";
			setFrame(frames);
		}
//		else if(getOverrideContext()[0].equals("No Frame")){
//			browserSession.getDriver().switchTo().defaultContent();
//		}
		else {
			String[] frames = { "iframe[name='contentFrame']", "iframe[id='mainModalFrame']" };
			setFrame(frames);
		}
		
	}
		public String[] homeMenu() {
			return null;
		}
		
		public WebElement getFGNameonPriceTAM(String FGName) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + FGName + "')]"));
		}
		
		public WebElement getItemonPriceTAM(String itemNumber) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + itemNumber + "')]"));
		}
		
		public WebElement getSupplierName(String Supplier) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + Supplier + "')]"));
		}
		
		public WebElement getCostType(String CostType) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + CostType + "')]"));
		}
		
		public WebElement getDestination(String Destination) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + Destination + "')]"));
		}
		
		public WebElement getMPN1(String mpn1) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + mpn1 + "')]"));
		}
		
		public WebElement getMPN2(String mpn2) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[2]//div[contains(text(),'" + mpn2 + "')]"));
		}
		
		public WebElement getSiteTAM(String SiteTAM) {
			return get(By.xpath("//div[@class='eto-grid-frozen']//tr[1]//div[contains(text(),'" + SiteTAM + "')]"));
		}
		
		public WebElement getPriceValue(String PriceValue) {
			return get(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//div[contains(text(),'" + PriceValue + "')]"));
		}
		
		public WebElement downloadIcon(String filedownload) {
			return get(By.xpath("//button[@title='File Download']//i[contains(text(),'" + filedownload + "')]"));
		}
		
		public WebElement getTAMSite() {
	        return get(By.xpath("//input[contains(@name,'value(tamSite)')]"));
	    }
	    
	    public WebElement getMultipleSite() {
	        return get(By.xpath("//textarea[contains(@name,'multipleTamSites')]"));
	    }
	  
	    public WebElement getMultipleFG() {
	        return get(By.xpath("//textarea[contains(@name,'value(groupNames)')]"));
	    }
	    
	    public WebElement getMultipleItems() {
	        return get(By.xpath("//textarea[contains(@name,'value(itemNumbers)')]"));
	    }
	    
	    public WebElement getTamEixts() {
	        return get(By.xpath("//select[contains(@name,'value(tamExists)')]"));
	    }
	    
		
	}

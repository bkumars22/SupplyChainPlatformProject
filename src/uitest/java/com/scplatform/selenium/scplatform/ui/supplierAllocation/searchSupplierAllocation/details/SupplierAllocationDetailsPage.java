/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class SupplierAllocationDetailsPage extends SCPlatformPage {

	public By tableLocator()	{
		return By.id("saDetailTable");
	}
	
	/**
	 * @return	Parses the results listing and returns a list of {@link SupplierAllocationDetailsModel} data
	 */
	public List<SupplierAllocationDetailsModel> parseResults() {
    	List<SupplierAllocationDetailsModel> tableData = new ArrayList<SupplierAllocationDetailsModel>();
      	SupplierAllocationDetailsModel data = new SupplierAllocationDetailsModel();
      	 	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	parsedTableData = this.tableParse(tableLocator());
    	
      	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("supplierItemNumber"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("supplierItemNumber"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("supplierItemNumber")).size();
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DateFormatUI());
    	
    	// last row is a Total line
    	for (int row = 1; row < tableSize-1; row++)	{
    		data = new SupplierAllocationDetailsModel();
    		
    		try	{
        		data.setSupplierItemNumber(parsedTableData.get(data.getDisplayName("supplierItemNumber")).get(row));
        		data.setSupplier(parsedTableData.get(data.getDisplayName("supplier")).get(row));
        		data.setSupplierSite(parsedTableData.get(data.getDisplayName("supplierSite")).get(row));
        		data.setAllocation(Float.parseFloat(parsedTableData.get(data.getDisplayName("allocation")).get(row)));
        		data.setPrice(parsedTableData.get(data.getDisplayName("price")).get(row));
        		data.setDescription(parsedTableData.get(data.getDisplayName("description")).get(row));
        		data.setPeriodStart(DateTime.parse(parsedTableData.get(data.getDisplayName("periodStart")).get(row), formatter));
        		data.setPeriodEnd(DateTime.parse(parsedTableData.get(data.getDisplayName("periodEnd")).get(row), formatter));
        		data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
        		tableData.add(data);  // ← ADD ONLY ON SUCCESSFUL PARSE
    		} catch (NullPointerException | IndexOutOfBoundsException e){
    			JLog.error("Error getting data for row " + row + " - skipping incomplete record: " + e.getMessage(), e, TakeScreenshot.True);
    			// ← Don't add incomplete data, continue to next row
    		}
    		

    		
    		tableData.add(data);
    	}
    	
		return tableData;
	}
	

	public WebElement button_New()	{
		return getElement(By.partialLinkText("New"));
	}
	
	public WebElement button_Delete()	{
		return getElement(By.partialLinkText("Delete"));
	}
	
	public WebElement button_Reload()	{
		return getElement(By.partialLinkText("Reload"));
	}
	
	public WebElement button_Back()	{
		return getElement(By.partialLinkText("Back"));
	}
	
	public WebElement button_SaveAndExit()	{
		return getElement(By.partialLinkText("Save and Exit"));
	}
	
	public WebElement button_Save()	{
		return getElement(By.partialLinkText("Save"));
	}
	

}

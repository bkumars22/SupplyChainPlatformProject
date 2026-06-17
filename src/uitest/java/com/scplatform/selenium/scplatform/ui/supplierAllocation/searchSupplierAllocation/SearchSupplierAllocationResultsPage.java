/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.openqa.selenium.By;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;

/**
 * Navigation: Supply Allocation -> Search Supply Allocation
 * 
 * @author dgenrich
 * @see #parseResults()
 */
public class SearchSupplierAllocationResultsPage extends SCPlatformSearchResultsPage {

	public By tableLocator()	{
		return By.id("saSearchResultTable_data");
	}
	
	/**
	 * @return	Parses the results listing and returns a list of {@link SearchSupplierAllocationResultsModel} data
	 */
	public List<SearchSupplierAllocationResultsModel> parseResults() {
    	List<SearchSupplierAllocationResultsModel> tableData = new ArrayList<SearchSupplierAllocationResultsModel>();
       	SearchSupplierAllocationResultsModel data = new SearchSupplierAllocationResultsModel();
 	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
      	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(data.getDisplayName("itemNumber"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + data.getDisplayName("itemNumber"), TakeScreenshot.True);
    		return tableData;
    	}
    	int tableSize = parsedTableData.get(data.getDisplayName("itemNumber")).size();
    	DateTimeFormatter formatter = DateTimeFormat.forPattern(Constants.DateFormatUI());
    	
    	for (int row = 1; row < tableSize; row++)	{
    		data = new SearchSupplierAllocationResultsModel();
    		
    		try	{
        		data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
        		data.setSupplierItemNumber(parsedTableData.get(data.getDisplayName("supplierItemNumber")).get(row));
        		data.setSupplier(parsedTableData.get(data.getDisplayName("supplier")).get(row));
        		data.setSupplierSite(parsedTableData.get(data.getDisplayName("supplierSite")).get(row));
        		data.setAllocation(Float.parseFloat(parsedTableData.get(data.getDisplayName("allocation")).get(row)));
        		data.setPeriodStart(DateTime.parse(parsedTableData.get(data.getDisplayName("periodStart")).get(row), formatter));
        		data.setPeriodEnd(DateTime.parse(parsedTableData.get(data.getDisplayName("periodEnd")).get(row), formatter));
        		tableData.add(data);  // ← ADD ONLY ON SUCCESSFUL PARSE
    		} catch (NullPointerException | IndexOutOfBoundsException e){
    			JLog.error("Error getting data for row " + row + " - skipping incomplete record: " + e.getMessage(), e, TakeScreenshot.True);
    			// ← Don't add incomplete data, continue to next row
    		}
    	}
    	
		return tableData;
	}
	
}

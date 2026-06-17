/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsPage;
import com.test.selenium.scplatform.ui.costing.searchCostRecords.SearchCostRecordsResultsModel.Range;

public class SearchCostRecordsResultsPage extends SCPlatformSearchResultsPage {
	protected SearchCostRecordsResultsModel singleRow;
	protected List<Range> range;
	protected Range singleRange;
	
	
	public By tableLocator()	{
		return By.id("crSummarySearchResultTable_data");
	}
	
	public List<SearchCostRecordsResultsModel> parseResults() {
    	List<SearchCostRecordsResultsModel> tableData = new ArrayList<SearchCostRecordsResultsModel>();
    	
    	HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();
    	singleRow = new SearchCostRecordsResultsModel();
    	
    	if (!this.getResultsFoundCount().equals("0"))	{
    		parsedTableData = this.tableParse(tableLocator());
    	}
    	
    	if (parsedTableData == null)	{
    		JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
    		return tableData;
    	}
    	if (!parsedTableData.containsKey(singleRow.getDisplayName("status"))){
    		JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: " + singleRow.getDisplayName("status"), TakeScreenshot.True);
    		return tableData;
    	}
    	
    	int tableSize = parsedTableData.get(singleRow.getDisplayName("status")).size();
   		int index = 0;
   		
		for (int row = 1; row < tableSize; row++){
			String itemName = "";
			if (isItemValid(parsedTableData.get(singleRow.getDisplayName("item")), row))	{
				itemName = parsedTableData.get(singleRow.getDisplayName("item")).get(row);
			}

			if (itemName.equals(""))	{
				// data has a range set
				index++;
				singleRange = singleRow.new Range();
			} else 	{
				// have a new item
				
				// add data for previous item
				if (singleRow != null){
					singleRow.setRange(range);
					tableData.add(singleRow);
				}
				
				// setup variables
				index = 0;
				
				singleRow = new SearchCostRecordsResultsModel();
				range = new ArrayList<Range>();
				singleRange = singleRow.new Range();
			}
			
			for (String header : parsedTableData.keySet()){

				ArrayList<String> data = parsedTableData.get(header);
				
				set(header, data.get(row), index);
			}
			
			range.add(singleRange);

		}
		
		// add in the last data set
		singleRow.setRange(range);
		tableData.add(singleRow);

		return tableData;
	}
	
	
	
	protected boolean isItemValid(ArrayList<String> itemColumn, int row) {
		
		if ( (itemColumn != null) && (itemColumn.size() > row) )	{
			if (StringUtils.isNotBlank(itemColumn.get(row)))	{
				String item = itemColumn.get(row);
				if (item.contains("-"))	{
					return true;
				} else	{
					String parsed = item;
					if (item.contains("."))	{
						if (StringUtils.isNumeric(item.split("\\.")[0]))	{
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	protected void set (String header, String value, int index){
		float floatValue = NullValue.FLOAT;
		
		if (index == 0)	{
			if (header.equals("Status"))	{
				singleRow.setStatus(value); 
			} else if (header.equals("Commodity Name"))	{
				singleRow.setCommodityName(value);
			} else if (header.equals("Item"))	{
				singleRow.setItem(value);
			} else if (header.equals("Item Description"))	{
				singleRow.setItemDescription(value);
			} else if (header.equals("Item Business"))	{
				singleRow.setItemBusiness(value);
			} else if (header.equals("Supplier"))	{
				singleRow.setSupplier(value);
			} else if (header.equals("Source Site"))	{
				singleRow.setSourceSite(value);
			} else if (header.equals("Destination Site"))	{
				singleRow.setDestinationSite(value);
			} else if (header.equals("Cost Type"))	{
				singleRow.setCostType(value);
			} else if (header.equals("Pricing Scenario"))	{
				singleRow.setPricingScenario(value);
			} else if (header.equals("Start Date"))	{
				singleRow.setStartDate(value);
			} else if (header.equals("End Date"))	{
				singleRow.setEndDate(value);
			} else if (header.equals("Currency"))	{
				singleRow.setCurrency(value);
			} else if (header.equals("Product State"))	{
				singleRow.setProductState(value);
			} else if (header.equals("Non-Managed Cost Adjustment"))	{
				singleRow.setNonManagedCostAdjustment(value);
			} else if (header.equals("Lane Name"))	{
				singleRow.setLaneName(value);
			} else if (header.equals("Responsibility"))	{
				singleRow.setResponsibility(value);
			} else if (header.equals("Comment"))	{
				singleRow.setComment(value);
			} else if (header.equals("Reason Code"))	{
				singleRow.setReasonCode(value);
			} 
		}
		
		if (header.equals("From"))	{
			if (value.trim().equals(""))	{
				floatValue = NullValue.FLOAT;
			} else if (value.startsWith("*"))	{
				value = value.substring(1);
				floatValue = Float.parseFloat(value);
			} else	{
				floatValue = Float.parseFloat(value);
			}
			singleRange.setCostRecordRange_from(floatValue);
		} else if (header.equals("To"))	{
			if (value.trim().equals(""))	{
				floatValue = NullValue.FLOAT;
			} else	{
				floatValue = Float.parseFloat(value);
			}
			singleRange.setCostRecordRange_to(floatValue);
		} else if (header.equals("Total"))	{
			if (value.trim().equals(""))	{
				floatValue = NullValue.FLOAT;
			} else	{
				floatValue = Float.parseFloat(value);
			}
			singleRange.setCostTotal(floatValue);
		} else {
			if (singleRange.getCostElementList() == null){
				singleRange.setCostElementListToDefaultValues();
			}
			
			if (singleRange.getCostElementList().containsKey(header))	{
				if (value.trim().equals(""))	{
					floatValue = NullValue.FLOAT;
				} else	{
					floatValue = Float.parseFloat(value);
				}
				
				singleRange.setCostElementList(header, floatValue);
			}

		}
		
	}
	
	
}

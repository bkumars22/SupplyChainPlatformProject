/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.sourcingLane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.calendar.CalendarUtils;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.parser.ItemData;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecord;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordRange;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordValue;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostValueDetail;
import com.test.selenium.scplatform.resources.Config;

public class SourcingLaneGenerator<T extends SourcingLane> {
	protected List<T> dataSet; 
	protected List<Partner> allCompanies;
	protected List<Item> itemList;
	protected List<ItemData> itemData;
	protected int ExternalIdIndex = 0;
	protected String ExternalId;
	protected String costType;
	protected Iterable<Calendar> calendar;
	protected float lastMaterialCost = NullValue.FLOAT;
	
	public SourcingLaneGenerator()	{
		dataSet = new ArrayList<T>();
	}
	
	public List<T> build(List<Item> itemList, List<ItemData> itemData, Iterable<Calendar> calendar, List<Partner> allCompanies)	{
		this.itemList = itemList;
		this.itemData = itemData;
		this.allCompanies = allCompanies;
		this.calendar = calendar;
		
		ExternalIdIndex = 0;
		for (int index = 0; index < this.itemData.size(); index++){
			if (this.itemData.get(index).getItemNumber().contains(" BOM"))	{
				// skip, this is a BOM
				continue;
			}
			buildSourcingLane (this.itemData.get(index));
		}

		return dataSet;
	}
	
	
	protected void buildSourcingLane(ItemData excelData) {
		List<Partner> suppliers = getSuppliers(excelData.getSuppliers());
		Partner hubCompany = getEnterpriseCompany();
		String date = DateTime.now().toString("MMddHHmm");
		
		for (int index = 0; index < suppliers.size(); index++)	{
			ExternalIdIndex++;
			ExternalId = date + ExternalIdIndex;
			
			T sourcingLaneData = (T) T.Factory.newInstance();
			Partner partner = suppliers.get(index);

			Item item = getItem(excelData.getItemNumber());
			if (item == null){
				// no data
				continue;
			}
			
			StringBuffer sourcingLane = new StringBuffer();
			sourcingLane.append("sourcingLane.");
			sourcingLane.append(excelData.getItemNumber() + ".");
			sourcingLane.append(partner.getName() + ".");
			sourcingLane.append(partner.getUdf1() + ".");
			sourcingLane.append(item.getDataSource() );
			
			sourcingLaneData.setSourcingLaneIdentifier(sourcingLane.toString());
			sourcingLaneData.setSourcingLaneExternalId(sourcingLaneData.getSourcingLaneIdentifier() + "." + ExternalId);
			sourcingLaneData.setDescription(item.getDescription());
			sourcingLaneData.setComment("Comment for Item - " + excelData.getItemNumber()); 
			sourcingLaneData.setItemIdentifier(item.getItemIdentifier());
			sourcingLaneData.setItemUniqueId(item.getItemUniqueId());
			sourcingLaneData.setItemRevision(item.getRevision());
			sourcingLaneData.setItemVersion(item.getVersion()); 
			sourcingLaneData.setBusinessEntity(hubCompany.getName());
			sourcingLaneData.setBusinessEntityType(item.getBusinessEntityType());
			
			// Site needs to be null for the allocation values to appear in the sourcing lane
			// restored site, since it seems it is needed on everything except Sourcing Lane XML upload
			sourcingLaneData.setSite(hubCompany.getUdf1());
			
			String fromBusinessEntity = partner.getDuns();
			String fromBusinessEntityType = "SUPPLIER";
			
			if ((item.getApprovedVendorListItem() != null) && (!item.getApprovedVendorListItem().isEmpty()) )	{
				for (int i = 0; i < item.getApprovedVendorListItem().size(); i++){
					if (item.getApprovedVendorListItem().get(i).getSite().equals(partner.getUdf1())){
						fromBusinessEntity = item.getApprovedVendorListItem().get(i).getVendorBusinessEntity();
						fromBusinessEntityType = item.getApprovedVendorListItem().get(i).getVendorBusinessEntityType();
						break;
					}
				}
			}
			sourcingLaneData.setFromBusinessEntity(fromBusinessEntity);
			sourcingLaneData.setFromBusinessEntityType(fromBusinessEntityType); 
			
			sourcingLaneData.setSite(hubCompany.getSite());
			sourcingLaneData.setFromSite(partner.getSite());
			sourcingLaneData.setLifeCycleCode((item.getLifeCycleCode() == null) ? "PRODUCTION" : item.getLifeCycleCode());
			sourcingLaneData.setDateOffset(RandomUtils.rand(3, 10));
			sourcingLaneData.setCurrencyCode("USD");
			sourcingLaneData.setOwnerName(item.getContactName()); 
			sourcingLaneData.setState("PENDING");
			sourcingLaneData.setLastStateChangeBy(item.getContactName());
			sourcingLaneData.setLastStateChangeOn(DateTime.now().minusDays(RandomUtils.rand(10, 40)));
			sourcingLaneData.setLastUpdateDate(sourcingLaneData.getLastStateChangeOn().plusDays(RandomUtils.rand(0, 5)));
			sourcingLaneData.setOperationCode("A"); 
			
			sourcingLaneData.setCostRecord(buildCostRecord (item, excelData, sourcingLaneData)); 
			
			if (sourcingLaneData.getFromBusinessEntity().equals(sourcingLaneData.getBusinessEntity()))	{
				sourcingLaneData.setFromBusinessEntityType(sourcingLaneData.getBusinessEntityType());
			}
			

			
			// RULE
			// FromSite must be empty if CostType is SELL,LIST,EMQUOTE
			List<String> fromSiteCostTypes = getFromSiteCostTypes();
			if (fromSiteCostTypes.contains(costType))	{
				sourcingLaneData.setFromSite(null);
			}
			
			// RULE
			// If BusinessEntityType is not Enterprise, then CostType can only be LIST or EMQOUTE.
			List<String> businessEntityTypeCostType = getBusinessEntityTypeCostType();
			if (!sourcingLaneData.getBusinessEntityType().equals("ENTERPRISE")){
				if (!businessEntityTypeCostType.contains(costType))	{
					JLog.error (this.getClass().getSimpleName() + ".buildSourcingLane() BusinessEntityType=" + sourcingLaneData.getBusinessEntityType() + 
							" and CostType=" + costType.toString() + 
							".  Rule is If BusinessEntityType is not Enterprise, then CostType can only be: " + businessEntityTypeCostType.toString());

				}
			}

			// RULE
			// If BusinessEntityType=ENTERPRISE, CostType must be one of: BUY,SELL,ODMBUY,SERVICE,EMQUOTE
			List<String> ENTERPRISETypeCostType = getENTERPRISETypeCostType();
			if (sourcingLaneData.getBusinessEntityType().equals("ENTERPRISE")){
				if (!ENTERPRISETypeCostType.contains(costType))	{
					JLog.error (this.getClass().getSimpleName() + ".buildSourcingLane() BusinessEntityType=" + sourcingLaneData.getBusinessEntityType() + 
							" and CostType=" + costType.toString() + 
							".  Rule is If BusinessEntityType=Enterprise, then CostType can only be: " + ENTERPRISETypeCostType.toString());

				}
			}
			
			
			this.dataSet.add(sourcingLaneData);
		}
	}
	
	
	protected List<Partner> getSuppliers(String suppliers){
		List<Partner> supplierList = new ArrayList<Partner>();
		
		String[] supplierArray = suppliers.split(",");
		
		for (int index = 0; index < supplierArray.length; index++)	{
			for (Partner company : allCompanies){
				if (company.getSite().equals(supplierArray[index]))	{
					supplierList.add(company);
				}
			}
		}
		
		return supplierList;
	}
	
	public List<String> getCostElementType(String costElementType)	{
		List<String> costTypeList = new ArrayList<String>();
		String[] costTypeArray = costElementType.split(",");
		
		for (int index = 0; index < costTypeArray.length; index++)	{
			costTypeList.add(costTypeArray[index]);
		}
		
		return costTypeList;
	}
	
	
	protected Partner getEnterpriseCompany()	{
		Partner partner = null;
		
		for (Partner company : allCompanies){
			if (company.getUdf2().equals("GLOBAL"))	{
				partner = company;
				break;
			}
		}
		return partner;
	}
	
	protected Item getItem(String itemName){
		Item item = null;
		
		for (Item data : this.itemList)	{
			if (data.getItemIdentifier().equals(itemName))	{
				item = data;
				break;
			}
		}
		
		return item;
	}

	/**
	 * RULE:<br>
	 * FromSite must be empty if CostType is SELL,LIST,EMQUOTE
	 * 
	 * @return ArrayList of the CostType values.
	 */
	protected List<String> getFromSiteCostTypes()	{
		List<String> fromSiteCostTypes = new ArrayList<String>();
		
		fromSiteCostTypes.add("EMQUOTE");
		fromSiteCostTypes.add("LIST");
		fromSiteCostTypes.add("SELL");
		
		return fromSiteCostTypes;
	}
	
	/**
	 * RULE:<br>
	 * If BusinessEntityType is not Enterprise, then CostType can only be LIST or EMQOUTE.
	 * 
	 * @return rrayList of the CostType values.
	 */
	protected List<String> getBusinessEntityTypeCostType()	{
		List<String> businessEntityTypeCostType = new ArrayList<String>();
		
		businessEntityTypeCostType.add("EMQUOTE");
		businessEntityTypeCostType.add("LIST");
		
		return businessEntityTypeCostType;
	}
	
	/**
	 * RULE:<br>
	 * If BusinessEntityType=ENTERPRISE, CostType must be one of: BUY,SELL,ODMBUY,SERVICE,EMQUOTE
	 * 
	 * @return ArrayList of the CostType values.
	 */
	protected List<String> getENTERPRISETypeCostType()	{
		List<String> costTypes = new ArrayList<String>();
		
		costTypes.add("BUY");
		costTypes.add("SELL");
		costTypes.add("ODMBUY");
		costTypes.add("SERVICE");
		costTypes.add("EMQUOTE");
		
		return costTypes;
	}
	
	/**
	 * RULE:<br>
	 * CostElementType=MATERIAL only if CostType is BUY,EMQUOTE,LIST,SELL,ODMBUY,SERVICE
	 * 
	 * @return	ArrayList of the CostType values.
	 */
	protected ArrayList<String> getMaterialCostTypes()	{
		ArrayList<String> materialCostTypes = new ArrayList<String>();
		
		materialCostTypes.add("BUY");
		materialCostTypes.add("EMQUOTE");
		materialCostTypes.add("LIST");
		materialCostTypes.add("SELL");
		materialCostTypes.add("ODMBUY");
		materialCostTypes.add("SERVICE");
		
		return materialCostTypes;
	}
	
	protected List<CostRecord> buildCostRecord(Item itemData, ItemData excelData, SourcingLane sourcingLaneData) {
		List<CostRecord> costRecord = new ArrayList<CostRecord>();
		CostRecord costRecordStructure = CostRecord.Factory.newInstance();
		
		Calendar calendar = getCalendarDate(DateTime.now().withTimeAtStartOfDay());
		if (calendar == null){
			JLog.error(this.getClass() + ".buildCostRecord() - Unable to find Calendar for current date: " + DateTime.now().withTimeAtStartOfDay());
			calendar = Calendar.Factory.newInstance();
			calendar.setMonth_StartDate(DateTime.now().minusMonths(1));
			calendar.setMonth_EndDate(DateTime.now().plusMonths(1));
		}
		
		costRecordStructure.setCostRecordExternalId("cost-record-" + itemData.getItemIdentifier() + "." + ExternalId);
		costRecordStructure.setDescription("cost-record-description-" + itemData.getItemIdentifier());
		costRecordStructure.setComment("Cost Record for " + itemData.getItemIdentifier()); 
		costRecordStructure.setCostType(excelData.getCostType());
		costType = excelData.getCostType();
		costRecordStructure.setCostProviderBusinessEntity("");
		costRecordStructure.setCostProviderBusinessEntityType(""); 
		costRecordStructure.setEffectiveFromDate(calendar.getMonth_StartDate().withTimeAtStartOfDay());
		costRecordStructure.setEffectiveToDate(calendar.getMonth_EndDate().withTimeAtStartOfDay()); 
		costRecordStructure.setState("Production");
		costRecordStructure.setLastStateChangeBy(itemData.getContactName());
		costRecordStructure.setLastStateChangeOn((costRecordStructure.getEffectiveFromDate() == null) ? 
				DateTime.now().minusDays(RandomUtils.rand(1, 5)) :
					costRecordStructure.getEffectiveFromDate().minusDays(RandomUtils.rand(1, 5))); 
		costRecordStructure.setLastUpdateDate(costRecordStructure.getLastStateChangeOn().plusDays(RandomUtils.rand(0, 5))); 
		costRecordStructure.setOperationCode("A");
		costRecordStructure.setPricingScenario(excelData.getPricingScenario()); 
		costRecordStructure.setReasonCode(getRandomReasonCode(costType));
		
		if (excelData.getPricingScenario().equals("Volume Based"))	{
			costRecordStructure.setCostRecordRange(buildCostRangeRecord(itemData, excelData, sourcingLaneData));
		} else	{
			costRecordStructure.setCostRecordValue(buildCostRecordValue(itemData, excelData, sourcingLaneData));
		}
		

		costRecord.add(costRecordStructure);
		
		return costRecord;
	}

	protected String getRandomReasonCode(String costType) {
		Config.downloadResource();
		List<String> availableReasonCodes = Config.getMultipleText("pcm.costRecord.reasonCode");
		String[] requiredCostTypes = Config.getText("pcm.costRecord.reasonCode.required.types").split(",");
		List<String> costTypeList = Arrays.asList(requiredCostTypes);  
		
		String reasonCode = null;
		
		if (costTypeList.contains(costType.toString()))	{
			int index = RandomUtils.rand(0, availableReasonCodes.size() - 1);
			reasonCode = availableReasonCodes.get(index);
		}
				
		return reasonCode;
	}
	
	protected Calendar getCalendarDate (DateTime date)	{
		CalendarUtils utils = new CalendarUtils();
		return utils.findByDate(this.calendar, date);
	}
	
	protected List<CostRecordRange> buildCostRangeRecord(
			Item itemData, 
			ItemData excelData,
			SourcingLane sourcingLaneData) {
		
		List<CostRecordRange> costRecordRangeList = new ArrayList<CostRecordRange>();
		
		CostRecordRange data = CostRecordRange.Factory.newInstance();
		data.setFromRange(0f);
		data.setToRange(50f); 
		data.setIsActive("true");
		data.setCostRecordValue(buildCostRecordValue(itemData, excelData, sourcingLaneData));
		costRecordRangeList.add(data);
		
		data = CostRecordRange.Factory.newInstance();
		data.setFromRange(51f);
		data.setToRange(150f); 
		data.setIsActive("true");
		data.setCostRecordValue(buildCostRecordValue(itemData, excelData, sourcingLaneData));
		costRecordRangeList.add(data);
		
		data = CostRecordRange.Factory.newInstance();
		data.setFromRange(151);
		data.setToRange(NullValue.FLOAT); 
		data.setIsActive("true");
		data.setCostRecordValue(buildCostRecordValue(itemData, excelData, sourcingLaneData));
		costRecordRangeList.add(data);
		
		return costRecordRangeList;
	}

	
	protected List<CostRecordValue> buildCostRecordValue(
			Item itemData, 
			ItemData excelData,
			SourcingLane sourcingLaneData) {

		List<CostRecordValue> costRecordValueList = new ArrayList<CostRecordValue>();

		List<String> costElementList = getCostElementType(excelData.getCostElementType());
		
		for (int index = 0; index < costElementList.size(); index++){
			String costElementType = costElementList.get(index);
			
			if (badCostElementType(costElementType))	{
				continue;
			}
			
			CostRecordValue costRecordValueStructure = CostRecordValue.Factory.newInstance();
			
			costRecordValueStructure.setCostElementType(costElementType);
			
			float costValue = getCostValue(costElementType);
			costRecordValueStructure.setCostValue(Float.toString(costValue));
			costRecordValueStructure.setCostUnitofMeasureCode("each");
			costRecordValueStructure.setCostValueDetail(buildCostValueDetail(costElementType, costValue));
			
			// RULES
			// CostElementType=MATERIAL only if CostType is BUY,EMQUOTE,LIST,SELL,ODMBUY,SERVICE
			List<String> materialCostTypes = getMaterialCostTypes();
			if (costElementType.equals("MATERIAL"))	{
				if (!materialCostTypes.contains(this.costType))	{
					JLog.error (this.getClass().getSimpleName() + ".buildCostRecordValue() CostElementType=" + costElementType + 
							" and costType=" + costType.toString() + ".  Rule is that costElementType must be one of: " + materialCostTypes.toString());
				}
			}
			costRecordValueList.add(costRecordValueStructure);
		}

		
		
		return costRecordValueList;
	}
	
	
	protected boolean badCostElementType (String costElementType){
		ArrayList<String> badElements = new ArrayList<String>();
		badElements.add("PROFITMARGIN");		
		return badElements.contains(costElementType);
	}
	
	protected float getCostValue(String costElementType) {
		float costValue = 0f;
		
		if (costElementType.equals("NPITOOLING"))	{
			costValue = RandomUtils.randomFloat(4, 9);
		} else if (costElementType.equals("MATERIAL"))	{
			costValue = RandomUtils.randomFloat(1, 20);
			lastMaterialCost = costValue;
		} else if (costElementType.equals("MVA"))	{
			costValue = RandomUtils.randomFloat(4, 9);
		} else if (costElementType.equals("PROFITMARGIN"))	{
			costValue = RandomUtils.randomFloat(4, 9);
		} else if (costElementType.equals("SGA"))	{
			costValue = RandomUtils.randomFloat(1, 20);
		} else if (costElementType.equals("VAT"))	{
			costValue = RandomUtils.randomFloat(1, 20);
		} else if (costElementType.equals("TAXES"))	{
			costValue = RandomUtils.randomFloat(1, 5);
		} else if (costElementType.equals("INFREIGHT"))	{
			costValue = RandomUtils.randomFloat(1, 5);
		} else if (costElementType.equals("OUTFREIGHT"))	{
			costValue = RandomUtils.randomFloat(1, 5);
		} else if (costElementType.equals("ROYALTY"))	{
			costValue = RandomUtils.randomFloat(3, 20);
		} else if (costElementType.equals("AMORTIZATION"))	{
			costValue = RandomUtils.randomFloat(1, 5);
		} else if (costElementType.equals("TRANSPORTATION"))	{
			costValue = RandomUtils.randomFloat(1, 30);
		} else if (costElementType.equals("OPEXNRE"))	{
			costValue = RandomUtils.randomFloat(2, 25);
		} else	{
			costValue = RandomUtils.randomFloat(2, 25);
		}
		return costValue;
	}

	protected List<CostValueDetail> buildCostValueDetail(String costElementType, float costValue) {

		List<CostValueDetail> costValueDetailList = null;

		if (costElementType.equals("TRANSPORTATION"))	{
			costValueDetailList = new ArrayList<CostValueDetail>();
			CostValueDetail data = CostValueDetail.Factory.newInstance();
			float percentage = 100f;
			
			data.setCostValueName("SEA");
			data.setCostValueValue(costValue);
			data.setCostValueBlend((float) RandomUtils.rand(5, 40));     // percentage
			percentage = percentage - data.getCostValueBlend();
			costValueDetailList.add(data);
			
			data = CostValueDetail.Factory.newInstance();
			data.setCostValueName("AIR");
			data.setCostValueValue(costValue);
			data.setCostValueBlend((float) RandomUtils.rand(5, 40));     // percentage
			percentage = percentage - data.getCostValueBlend();
			costValueDetailList.add(data);
			
			data = CostValueDetail.Factory.newInstance();
			data.setCostValueName("LAND");
			data.setCostValueValue(costValue);
			data.setCostValueBlend(percentage);     // percentage
			costValueDetailList.add(data);
		}
		
		return costValueDetailList;
	}
	
	
	
}

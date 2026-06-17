/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Partner;
import com.test.selenium.common.RandomUser;
import com.test.selenium.common.RandomUtils;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCode;
import com.test.selenium.scplatform.messages.item.parser.ItemData;
import com.test.selenium.scplatform.messages.item.subClasses.AlternateItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedManufacturerListItem;
import com.test.selenium.scplatform.messages.item.subClasses.ApprovedVendorListItem;
import com.test.selenium.scplatform.messages.item.subClasses.Bom;
import com.test.selenium.scplatform.messages.item.subClasses.BomLine;
import com.test.selenium.scplatform.messages.item.subClasses.BuyerCode;
import com.test.selenium.scplatform.messages.item.subClasses.ItemPlatform;
import com.test.selenium.scplatform.messages.item.subClasses.Responsibility;
import com.google.common.collect.Lists;



/**
 * This class is used by {@link ItemBuilder}
 * 
 * @author dgenrich
 *
 */
public class ItemGenerator<T extends Item> {
	protected boolean debugOutput = false;
	
	protected List<T> dataSet;
	protected List<ItemData> items;
	protected List<BusinessEntity> businessEntityList;
	protected List<CommodityCode> commodityCodeList;
	protected List<Partner> allCompanies;
	protected List<String> createdBoms;
	protected List<String> createdItems;
	protected boolean makeFullBOM = false;
	protected String topLevelBOM;
	protected ArrayList<Item> previousItemBuilder = null;
	
	
	public ItemGenerator()	{
		dataSet = new ArrayList<T>();
	}
	
	/**
	 * Builds the {@link Item} data
	 * @param businessEntityData
	 * 		{@link BusinessEntity} data.  Used to get details of the partners
	 * @param items
	 * 		
	 * @return
	 */
	public List<T> build(
			Iterable<BusinessEntity> businessEntityData, 
			Iterable<CommodityCode> commodityCodeData, 
			List<ItemData> items,
			List<Partner> allCompanies)	{
		
		this.items = items;
		this.businessEntityList = Lists.newArrayList(businessEntityData);
		this.commodityCodeList = Lists.newArrayList(commodityCodeData);
		this.allCompanies = allCompanies;
		
		createdBoms = new ArrayList<String>();
		createdItems = new ArrayList<String>();
		buildItemTopLevel ();
		buildBomTopLevel();
		
		return dataSet;
	}
	
	
	
	

	
	protected void buildItemTopLevel() {
		T itemData = null;
		BusinessEntity enterprise = getEnterprise();
		
		List<Integer> bomLevels = getBOMLevels();
		int total = bomLevels.size()-1;
		for (int x = total; x >= 0; x--){
			int level = bomLevels.get(x);
			RandomUser user = new RandomUser();
			String contact = user.getFirstName() + " " + user.getLastName();
			
			List<ItemData> itemsForLevel = getItemsForLevel (level);
			
			for (ItemData item : itemsForLevel){
				if (skipItem (item.getBuildAction()))	{
					continue;
				}
				
				
				itemData = (T) T.Factory.newInstance();
				
				writeDebugOutput ("DEBUG buildtemTopLevel[" + level + "]: " +  item.getItemNumber());
				
				itemData.setItemIdentifier(item.getItemNumber());
				itemData.setItemUniqueId(item.getItemNumber());
				itemData.setDescription(item.getDescription());
				
				if (createdItems.contains(itemData.getItemIdentifier()))	{
					// already created this item, skip
					continue;
				} else	{
					createdItems.add(itemData.getItemIdentifier());
				}
					
				itemData.setRevision(item.getRevision());
				itemData.setVersion(Integer.toString(RandomUtils.rand(1, 10)));
				itemData.setRevisionReleaseDate(DateTime.now().minusDays(RandomUtils.rand(40, 356)));
				itemData.setVersionReleaseDate(DateTime.now().minusDays(RandomUtils.rand(0, 39)));
				itemData.setLifeCycleCode(null);
				itemData.setLifeCycleCodeOther(null);
				itemData.setItemPartType("Item");
				itemData.setItemClassification("Classification " + item.getCommodityCode());
				itemData.setProprietaryProductFamily("ProductFamily " + item.getCommodityCode());
				itemData.setCommodityCode(item.getCommodityCode());
				itemData.setManagedBy(enterprise.getBusinessEntity());
				itemData.setProductUnitOfMeasureCode("each");
				itemData.setMakeBuy((level == 1) ? "Make" : "Buy");
				itemData.setMakeBuyOther(null);
				itemData.setIsSerializationRequired("false");
				itemData.setIsCertificationRequired("false");
				itemData.setOwnerName(null);
				itemData.setOwnerContactUniqueId(null);
				itemData.setContactName(StringUtils.isNoneEmpty(enterprise.getContactName()) ? enterprise.getContactName() : contact);
				itemData.setContactUniqueId(enterprise.getContactUniqueId());
				itemData.setIsTopLevel((level == 1) ? "true" : "false");
				itemData.setDataSource(enterprise.getDataSource());
				itemData.setBusinessEntityType("ENTERPRISE");
				itemData.setBusinessEntity(enterprise.getBusinessEntity());
				itemData.setEffectiveFromDate(itemData.getVersionReleaseDate().plusDays(RandomUtils.rand(0, 20)));
				itemData.setEffectiveToDate(DateTime.now().plusDays(RandomUtils.rand(10, 180)));
				itemData.setOperationCode("A");
				itemData.setLastUpdateDate(itemData.getEffectiveFromDate());
				itemData.setLevel(Integer.toString(level));
				
				itemData.setBom(new ArrayList<Bom>());
				itemData.setApprovedVendorListItem(new ArrayList<ApprovedVendorListItem>());;
				itemData.setAlternateItem(new ArrayList<AlternateItem>());
				itemData.setApprovedManufacturerListItem(new ArrayList<ApprovedManufacturerListItem>());
				itemData.setItemPlatform(buildItemPlatform(itemData.getCommodityCode(), itemData.getEffectiveFromDate(), itemData.getEffectiveToDate()));
				itemData.setBuyerCode(new ArrayList<BuyerCode>());
				itemData.setResponsibility(new ArrayList<Responsibility>());
				
				itemData.setApprovedVendorListItem(buildApprovedVendorListItem(itemData, item));
				
//				
//				if (level == 1){
//					itemData.Bom = buildTopBom (itemData, item);
//				} else	{
//					itemData.Bom = buildBom (itemData, item);
//					itemData.ApprovedVendorListItem = buildApprovedVendorListItem(itemData, item);
//				}
//
//
//				
				if (itemData.getIsTopLevel().equals("false")){
					topLevelBOM = item.getBomName();
				}
				
				this.dataSet.add(itemData);
				
				
				
			}
			
			
			
			
			// lowest level has no BOM
		}
		
	}
	
	protected void buildBomTopLevel() {
		
		BusinessEntity enterprise = getEnterprise();
		
		List<String> bomLevels = getBOMNames();
		if (bomLevels.contains(this.topLevelBOM))	{
			// move to the start of the list
			bomLevels.remove(this.topLevelBOM);
			bomLevels.add(0, this.topLevelBOM);
		}
		
		int total = bomLevels.size()-1;
		for (int x = total; x >= 0; x--){
			String bomName = bomLevels.get(x);
			
			if ((bomName.equals(this.topLevelBOM)) )	{
				makeFullBOM = true;
			}
			
			T itemData = (T) T.Factory.newInstance();
//			ItemsDataStructure itemStructure;
			
			List<ItemData> itemsForBom = getItemsForBom(bomName);			
			ItemData itemStructure = itemsForBom.get(0);
			
			itemData.setItemIdentifier(bomName);
			itemData.setItemUniqueId(bomName);
			itemData.setDescription(bomName + " Rollup");
			
			itemData.setRevision(itemStructure.getRevision());
			itemData.setVersion(Integer.toString(RandomUtils.rand(1, 10)));
			itemData.setRevisionReleaseDate(DateTime.now().minusDays(RandomUtils.rand(40, 356)));
			itemData.setVersionReleaseDate(itemData.getRevisionReleaseDate().plusDays(RandomUtils.rand(0, 39)));
			itemData.setLifeCycleCode(null);
			itemData.setLifeCycleCodeOther(null);
			itemData.setItemPartType("Item");
			itemData.setItemClassification(null);
			itemData.setProprietaryProductFamily(null);
			itemData.setCommodityCode(itemStructure.getCommodityCode());
			itemData.setManagedBy(enterprise.getBusinessEntity());
			itemData.setProductUnitOfMeasureCode(null);
			itemData.setMakeBuy(null);
			itemData.setMakeBuyOther(null);
			itemData.setIsSerializationRequired("false");
			itemData.setIsCertificationRequired("false");
			itemData.setOwnerName(null);
			itemData.setOwnerContactUniqueId(null);
			itemData.setContactName(null);
			itemData.setContactUniqueId(null);
			itemData.setIsTopLevel((itemStructure.getBomLevel() == 1) ? "true" : "false");
			itemData.setBusinessEntity(enterprise.getBusinessEntityName());
			itemData.setBusinessEntityType(enterprise.getBusinessEntityType());
			itemData.setDataSource(enterprise.getDataSource());
			itemData.setEffectiveFromDate(itemData.getVersionReleaseDate().plusDays(RandomUtils.rand(0, 20)));
			itemData.setEffectiveToDate(DateTime.now().plusDays(RandomUtils.rand(10, 180)));
			itemData.setOperationCode("A");
			itemData.setLastUpdateDate(itemData.getEffectiveFromDate());
			itemData.setLevel(Integer.toString(itemStructure.getBomLevel()));
					
			if (x == 0){
				itemData.setBom(buildBom(itemData, itemStructure));
			} else	{
				itemData.setBom(buildTopBom(itemData, itemStructure));
			}
			
			itemData.setApprovedVendorListItem(new ArrayList<ApprovedVendorListItem>());;
			itemData.setAlternateItem(new ArrayList<AlternateItem>());
			itemData.setApprovedManufacturerListItem(new ArrayList<ApprovedManufacturerListItem>());
			itemData.setItemPlatform(buildItemPlatform(itemData.getCommodityCode(), itemData.getEffectiveFromDate(), itemData.getEffectiveToDate()));
			itemData.setBuyerCode(new ArrayList<BuyerCode>());
			itemData.setResponsibility(new ArrayList<Responsibility>());					
			
			if (itemData.getBom() != null){
				if ( (itemData.getBom().get(0).getBomLine() == null) || (itemData.getBom().get(0).getBomLine().isEmpty()) )	{
					// don't load this BOM
					continue;
				}
			}
			this.dataSet.add(itemData);

			
		}
		
	}
	
	
	protected BusinessEntity getEnterprise()	{
		BusinessEntity businessEntity = null;
		
		for (BusinessEntity data : businessEntityList)	{
			if (data.getBusinessEntityType().equals("ENTERPRISE")){
				businessEntity = data;
				break;
			}
		}
		
		return businessEntity;
	}
	
	protected BusinessEntity getBusinessEntity (String supplier){
		BusinessEntity businessEntity = null;
		
		for (BusinessEntity data : businessEntityList)	{
			if (data.getBusinessEntityName().equals(supplier)){
				businessEntity = data;
				break;
			}
		}
		
		return businessEntity;
	}
	
	
	protected List<Integer> getBOMLevels(){
		List<Integer> bomLevels = new ArrayList<Integer>();

		for (int i = 0; i < this.items.size(); i++){
			if (!bomLevels.contains(this.items.get(i).getBomLevel())){
				bomLevels.add(this.items.get(i).getBomLevel());
			}
		}
		
		Collections.sort(bomLevels);
		
		return bomLevels;
	}
	
	
	protected List<ItemData> getItemsForLevel(int level){
		List<ItemData> itemsForLevel = new ArrayList<ItemData>();

		for (int i = 0; i < this.items.size(); i++){
			if (this.items.get(i).getBomLevel() == level){
				itemsForLevel.add(this.items.get(i));
			}
		}
		
		return itemsForLevel;
	}
	
	
	protected boolean skipItem(String buildAction) {
		if (buildAction.equals("BOMOnly"))	{
			return true;
		} else if (buildAction.equals("DoNotCreate"))	{
			return true;
		}
		return false;
	}
	
	
	protected void writeDebugOutput(String msg){
		if (debugOutput){
			JLog.write (msg);
		}
	}
	
	private List<ItemPlatform> buildItemPlatform(String commodityCode, DateTime effectiveFromDate, DateTime effectiveToDate) {
		List<ItemPlatform> itemPlatform = new ArrayList<ItemPlatform>();
		ItemPlatform platform = ItemPlatform.Factory.newInstance();
		
		platform.setPlatformName(getCommodityDescription(commodityCode));
		platform.setDescription("Platform Group for " + platform.getPlatformName());
		platform.setEffectiveFromDate(effectiveFromDate);
		platform.setEffectiveToDate(effectiveToDate);
		platform.setOperationCode("A");
		itemPlatform.add(platform);
		
		return itemPlatform;
	}
	
	
	protected String getCommodityDescription (String commodityCode){
		String desription = commodityCode;
		for (int index = 0; index < commodityCodeList.size(); index++){
			if (commodityCodeList.get(index).getCommodityCode().equals(commodityCode))	{
				desription = commodityCodeList.get(index).getDescription();
				break;
			}
		}
		
		
		return desription;
	}
	
	protected Partner getEnterpriseCompany()	{
		BusinessEntity enterprise = getEnterprise();
		Partner partner = null;
		
		for (Partner company : allCompanies){
			if (enterprise.getBusinessEntityName().equals(company.getName()))	{
				partner = company;
				break;
			}
		}
		return partner;
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
	
	
	protected List<ApprovedVendorListItem> buildApprovedVendorListItem(Item item, ItemData itemData) {
		List<ApprovedVendorListItem> approvedVendorListItem = new ArrayList<ApprovedVendorListItem>();
		List<Partner> supplierList = getSuppliers(itemData.getSuppliers());
		Partner hubCompany = getEnterpriseCompany();
		
		for (Partner supplier : supplierList){
		
			if (supplier == null){
				JLog.warning (this.getClass().getSimpleName() + ".buildApprovedVendorListItem() - unexpected NULL supplier!");
				continue;
			}
			BusinessEntity businessEntity = getBusinessEntity(supplier.getName());

			if (businessEntity == null)	{
//				JLog.error (this.getClass().getSimpleName() + ".buildApprovedVendorListItem() : Supplier=" + supplier.getName() + "; BusinessEntityStructure is NULL!");
				continue;
			}
			
			
			ApprovedVendorListItem data = ApprovedVendorListItem.Factory.newInstance();

			
			try	{
				data.setSiteName(supplier.getSite());
				data.setSite(supplier.getUdf1());
				data.setDescription(itemData.getDescription());
				data.setVendorBusinessEntity((businessEntity.getBusinessEntity() == null) ? hubCompany.getName() : businessEntity.getBusinessEntity());
				data.setVendorBusinessEntityType((businessEntity.getBusinessEntityType() == null) ? "ENTERPRISE" : businessEntity.getBusinessEntityType());
				data.setVendorItemIdentifier(itemData.getItemNumber() + "-" + data.getSiteName().toUpperCase());
				data.setVendorItemUniqueId(data.getVendorItemIdentifier());
				data.setVendorRevision(itemData.getRevision());
				data.setVendorVersion(Integer.toString(RandomUtils.rand(1, 10)));
				data.setVendorContactName(businessEntity.getContactName());
				data.setVendorContactUniqueId(businessEntity.getContactUniqueId());
				data.setPartStatusCode("Approved");
				data.setPartStatusCodeOther(null);
				data.setPreferredStatusCode(null);
				data.setPreferredStatusStartDate(null);
				data.setPreferredStatusEndDate(null);
				data.setOperationCode("A");
				data.setLastUpdateDate(DateTime.now().minusDays(RandomUtils.rand(10, 180)));
				
				approvedVendorListItem.add(data);
			} catch (NullPointerException e){
				JLog.warning (this.getClass().getSimpleName() + ".buildApprovedVendorListItem(): supplier=" + supplier);
				JLog.error (e);
			}
		}

		
		return approvedVendorListItem;
	}
	
	
	protected List<String> getBOMNames(){
		List<String> bomNames = new ArrayList<String>();

		for (int i = 0; i < this.items.size(); i++){
			if (!bomNames.contains(this.items.get(i).getBomName())){
				bomNames.add(this.items.get(i).getBomName());
			}
		}
		
		Collections.sort(bomNames);
		
		return bomNames;
	}
	
	protected List<ItemData> getItemsForBom(String bomName) {
		ArrayList<ItemData> subItems = new ArrayList<ItemData>();

		
		for (int i = 0; i < this.items.size(); i++){
			if (this.items.get(i).getBomName().equals(bomName)){
				subItems.add(this.items.get(i));
			}
		}
		
		return subItems;
	}
	
	
	protected List<Bom> buildBom(Item itemData, ItemData itemStructure) {
		if (skipBOM (itemStructure.getBuildAction()))	{
			return null;
		}
		
		List<Bom> bomData = new ArrayList<Bom>();
		ArrayList<ItemData> subItems = getSubItems(itemStructure.getBomName());
		
	
		Bom data = Bom.Factory.newInstance();
		
		data.setBomName(itemStructure.getBomName());
//		if (createdBoms.contains(data.bomName))	{
//			// Already created, move to next
//			continue;
//		}
//		createdBoms.add(data.bomName);
		writeDebugOutput("DEBUG buildBom: " + data.getBomName());
		
		data.setBomRevision(itemData.getRevision());
		data.setBomVersion(itemData.getVersion());
		data.setBomRevisionReleaseDate(itemData.getRevisionReleaseDate());
		data.setBomVersionReleaseDate(itemData.getVersionReleaseDate());
		data.setDescription("Rollup BOM for " + itemData.getItemIdentifier());
		data.setOwnerName(null);
		data.setOwnerContactUniqueIdentifier(null);
		data.setSite(null);
		data.setBillOfMaterialTypeCode(itemStructure.getBomType());	
		data.setBillOfMaterialTypeCodeOther(null);
		data.setEffectiveFromDate(itemData.getEffectiveFromDate());
		data.setEffectiveToDate(itemData.getEffectiveToDate());
		data.setOperationCode("A");
		data.setState("APPROVED");
		data.setLastStateChangeBy(null);
		data.setLastStateChangeOn(data.getBomVersionReleaseDate());
		data.setLastUpdateDate(itemData.getLastUpdateDate());
		data.setIsRepairs("true");
		
		data.setBomLine(buildBomLine(data, subItems));
		
		bomData.add(data);

		
		
		return bomData;
	}


	protected List<Bom> buildTopBom(Item itemData, ItemData itemStructure) {

		
		List<Bom> bomData = new ArrayList<Bom>();
		List<ItemData> subItems = getItemsForBom(itemStructure.getBomName());
		
		Bom data = Bom.Factory.newInstance();

		data.setBomName(itemStructure.getBomName());
		data.setBomRevision(itemData.getRevision());
		data.setBomVersion(itemData.getVersion());
		data.setBomRevisionReleaseDate(itemData.getRevisionReleaseDate());
		data.setBomVersionReleaseDate(itemData.getVersionReleaseDate()); 
		data.setDescription("Rollup for " + itemData.getItemIdentifier());
		data.setOwnerName(null);
		data.setOwnerContactUniqueIdentifier(null);;
		data.setSite(null);
		data.setBillOfMaterialTypeCode(itemStructure.getBomType());	
		data.setBillOfMaterialTypeCodeOther(null);
		data.setEffectiveFromDate(itemData.getEffectiveFromDate());
		data.setEffectiveToDate(itemData.getEffectiveToDate());
		data.setOperationCode("A");
		data.setState("APPROVED");
		data.setLastStateChangeBy(null);
		data.setLastStateChangeOn(data.getBomVersionReleaseDate());
		data.setLastUpdateDate(itemData.getLastUpdateDate());
		data.setIsRepairs("true");
		data.setBomLine(buildBomTopLine(data, subItems)); 
		bomData.add(data);

		
		return bomData;
	}

	
	protected boolean skipBOM(String buildAction) {
		if (makeFullBOM){
			return false;
		} else if (buildAction.equals("ItemOnly"))	{
			return true;
		} else if (buildAction.equals("DoNotCreate"))	{
			return true;
		}
		return false;
	}
	
	protected ArrayList<ItemData> getSubItems (String parentBOM)	{
		ArrayList<ItemData> subItems = new ArrayList<ItemData>();

		
		for (int i = 0; i < this.items.size(); i++){
			if (StringUtils.isNotBlank(this.items.get(i).getParentBOM()))	{
				if (this.items.get(i).getParentBOM().equals(parentBOM)){
					subItems.add(this.items.get(i));
				}
			}
		}
		
		return subItems;
	}
	
	
	protected List<BomLine> buildBomLine(Bom bomData, ArrayList<ItemData> subItems) {
		List<BomLine> bomLine = new ArrayList<BomLine>();
		if (subItems.isEmpty())	{
			return bomLine;
		}
		List<Partner> suppliers = getSuppliers(subItems.get(0).getSuppliers());
		
		BusinessEntity businessEntity = getBusinessEntity(suppliers.get(0).getName());
		if (businessEntity == null)	{
			JLog.error (this.getClass().getSimpleName() + ".buildBomLine() : Supplier=" + suppliers.get(0).getName() + "; BusinessEntityStructure is NULL!");
		}
		
		
		for (ItemData sub : subItems)	{
			
			BomLine line = BomLine.Factory.newInstance();
			

			writeDebugOutput("DEBUG buildBomLine: " + bomData.getBomName() + " -> " +  sub.getItemNumber());
			
			line.setItemIdentifier(sub.getItemNumber());
			line.setItemUniqueId(sub.getItemNumber());
			line.setItemRevision(sub.getRevision());
			line.setItemVersion(bomData.getBomVersion());
			line.setBusinessEntity(null);
			line.setBusinessEntityType(null);
			line.setManagedBy(businessEntity.getBusinessEntity());
//			line.setIsSerializationRequired("false");
			line.setBillOfMaterialTypeCode(sub.getBomType());	
			line.setBillOfMaterialTypeCodeOther(null);	
			line.setNotes(null);
			line.setItemQuantity(sub.getQuantity());
			line.setProductQuantityTypeCode(sub.getQuantityTypeCode());	
			line.setProductQuantityTypeCodeOther(null);
			line.setDescription(null);
			line.setProprietarySequenceIdentifier(NullValue.INTEGER);
			line.setEffectiveFromDate(bomData.getEffectiveFromDate());
			line.setEffectiveToDate(bomData.getEffectiveToDate());
			line.setOperationCode("A");	
			
			line.setApprovedVendorListItem(buildApprovedVendorListItem(line, sub));
			line.setApprovedManufacturerListItem(null);
			
			bomLine.add(line);
		}
		
		return bomLine;
	}
	
	protected List<BomLine> buildBomTopLine(Bom bomData, List<ItemData> subItems) {
		List<BomLine> bomLine = new ArrayList<BomLine>();
		ArrayList<String> boms = new ArrayList<String>();
		
		for (ItemData sub : subItems)	{
			if (skipBOM(sub.getBuildAction()))	{
				continue;
			}
			
			Item itemData = getItemsByName(sub.getItemNumber());
			
			
			BomLine line = BomLine.Factory.newInstance();
			
			if ( (StringUtils.isBlank(sub.getBomName())) || (topLevelBOM == null) || (topLevelBOM.equals(sub.getBomName())) )	{
				continue;
			}

			line.setItemIdentifier(itemData.getItemIdentifier());
			line.setItemUniqueId(itemData.getItemIdentifier());
			line.setItemRevision(itemData.getRevision());
			line.setItemVersion(itemData.getVersion());
			line.setBusinessEntity(itemData.getBusinessEntity());
			line.setBusinessEntityType(itemData.getBusinessEntityType());
			line.setManagedBy(itemData.getManagedBy());
//			line.setIsSerializationRequired(itemData.getIsSerializationRequired());
			line.setBillOfMaterialTypeCode(null);
			line.setBillOfMaterialTypeCodeOther(null);
			line.setNotes(null);
			line.setItemQuantity(sub.getQuantity());
			line.setProductQuantityTypeCode(sub.getQuantityTypeCode());
			line.setProductQuantityTypeCodeOther(null);
			line.setDescription(itemData.getDescription());
			line.setProprietarySequenceIdentifier(NullValue.INTEGER);
			line.setEffectiveFromDate(itemData.getEffectiveFromDate());
			line.setEffectiveToDate(itemData.getEffectiveToDate());
			line.setOperationCode(itemData.getOperationCode());
			
			line.setApprovedVendorListItem(null);
			line.setApprovedManufacturerListItem(null);
			
			bomLine.add(line);
		}

		return bomLine;
	}
	

	protected Item getItemsByName(String itemNumber){
		
		if (previousItemBuilder != null)	{
			for (int i = 0; i < this.previousItemBuilder.size(); i++){
				if (this.previousItemBuilder.get(i).getItemIdentifier().equals(itemNumber)){
					writeDebugOutput("DEBUG: getItemsByName: Found Previous Item: " + itemNumber);
					return this.previousItemBuilder.get(i);
				}
			}
		}
		
		for (int i = 0; i < this.dataSet.size(); i++){
			if (this.dataSet.get(i).getItemIdentifier().equals(itemNumber)){
				return this.dataSet.get(i);
			}
		}

		return null;
	}
	
	protected List<ApprovedVendorListItem> buildApprovedVendorListItem(BomLine bomLine, ItemData itemStructure) {
		List<ApprovedVendorListItem> approvedVendorListItem = new ArrayList<ApprovedVendorListItem>();
		List<Partner> supplierList = getSuppliers(itemStructure.getSuppliers());
		
		for (Partner supplier : supplierList){
			ApprovedVendorListItem data = ApprovedVendorListItem.Factory.newInstance();
			
			BusinessEntity businessEntity = getBusinessEntity(supplier.getName());
			if (businessEntity == null)	{
				JLog.error (this.getClass().getSimpleName() + ".buildApprovedVendorListItem() : Supplier=" + supplier.getName() + "; BusinessEntityStructure is NULL!");
			}
			
			data.setSite(businessEntity.getSite_Site());
			data.setDescription(itemStructure.getDescription());
			data.setVendorBusinessEntity(businessEntity.getBusinessEntity());
			data.setVendorBusinessEntityType(businessEntity.getBusinessEntityType());
			data.setVendorItemIdentifier(itemStructure.getItemNumber());
			data.setVendorItemUniqueId(itemStructure.getItemNumber() + "-" + data.getSiteName());
			data.setVendorRevision(itemStructure.getRevision());
			data.setVendorVersion(Integer.toString(RandomUtils.rand(1, 10)));
			data.setVendorContactName(businessEntity.getContactName());
			data.setVendorContactUniqueId(businessEntity.getContactUniqueId());
			data.setPartStatusCode("Approved");
			data.setPartStatusCodeOther(null);
			data.setPreferredStatusCode(null);
			data.setPreferredStatusStartDate(null);
			data.setPreferredStatusEndDate(null);
			data.setOperationCode("A");
			data.setLastUpdateDate(DateTime.now().minusDays(RandomUtils.rand(10, 180)));

			approvedVendorListItem.add(data);
		}

		
		return approvedVendorListItem;
	}

	
	
}


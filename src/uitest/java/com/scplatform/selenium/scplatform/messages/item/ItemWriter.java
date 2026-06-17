/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.constants.Constants;
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
 * @author dgenrich
 *
 * @param <T>	Item or any class that extends it
 * 
 * @see MessageWriter
 */
public class ItemWriter<T extends Item> extends MessageWriter<T> {
	protected final String messageType = "Item";
	protected final String messageVersion = "MCM1.0";
	
	protected List<T> messageData;
	protected String dateFormatString = null;
	
	/**
	 * @param messageClazz	
	 * 		The Item Message class, typically Item.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ItemBuilder}
	 */
	public ItemWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
	}
	
	
	@Override
	public String generate()	{
		
		try {
			openFile(saveToFile);
			buildHeader();
			
			for (int row = 0; row < messageData.size(); row++){
				node_Item(messageData.get(row));
			}
			
			out.write("</scplatform:ItemMessage>");
			closeFile();
		} catch (IOException e) {
			JLog.error(e);
		} 
		
		return saveToFile;
	}
	
	protected BufferedWriter out = null;
	protected void openFile(String saveToFileName) throws UnsupportedEncodingException, FileNotFoundException   {
        File file = new File(saveToFileName);
        if (file.exists())  {
            file.delete();
        }
        out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(saveToFileName), "UTF-8"));  
    }
	
	
	protected void closeFile() throws IOException   {
        if (out != null){
            out.close();
        }
    }
	
	protected void buildHeader() throws IOException	{
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		out.write("<scplatform:ItemMessage xmlns:scplatform=\"http://www.scplatform.local/E2openMCM\" ");
		out.write("headerVersion=\"1.0\" ");
		out.write("fromID=\"" + Constants.HubCompanyID + "\" ");
		out.write("toID=\"E2OPEN\" ");
		out.write("messageType=\"" + messageType + "\" ");
		out.write("messageVersion=\"" + messageVersion + "\" ");
		out.write("messageCount=\"0\" ");
		out.write("messageIndex=\"0\" ");
		out.write("loadRule=\"full\"");
		out.write(">");
		out.write("\n");
	}
	
	
	protected void node_Item(Item itemStructure) throws IOException {
		int tabCount = 1;
		
		out.write(tabs(tabCount) + "<scplatform:Item");
		
		if (itemStructure.getItemIdentifier() != null)	{
			out.write(" itemIdentifier=\"" + formatString(itemStructure.getItemIdentifier()) + "\"");
		}
		if (itemStructure.getItemUniqueId() != null)	{
			out.write(" itemUniqueId=\"" + formatString(itemStructure.getItemUniqueId()) + "\"");
		}
		if (itemStructure.getDescription() != null)	{
			out.write(" description=\"" + formatString(itemStructure.getDescription()) + "\"");
		}
		if (itemStructure.getLifeCycleCode() != null)	{
			out.write(" lifeCycleCode=\"" + itemStructure.getLifeCycleCode() + "\"");
		}
		if (itemStructure.getLifeCycleCodeOther() != null)	{
			out.write(" lifeCycleCodeOther=\"" + itemStructure.getLifeCycleCodeOther() + "\"");
		}
		if (itemStructure.getItemPartType() != null)	{
			out.write(" itemPartType=\"" + itemStructure.getItemPartType() + "\"");
		}
		if (itemStructure.getItemClassification() != null)	{
			out.write(" itemClassification=\"" + itemStructure.getItemClassification() + "\"");
		}
		if (itemStructure.getRevision() != null)	{
			out.write(" revision=\"" + itemStructure.getRevision() + "\"");
		}
		if (itemStructure.getVersion() != null)	{
			out.write(" version=\"" + itemStructure.getVersion() + "\"");
		}
		if (itemStructure.getRevisionReleaseDate() != null)	{
			out.write(" revisionReleaseDate=\"" + formatDate(itemStructure.getRevisionReleaseDate()) + "\"");
		}
		if (itemStructure.getVersionReleaseDate() != null)	{
			out.write(" versionReleaseDate=\"" + formatDate(itemStructure.getVersionReleaseDate()) + "\"");
		}
		if (itemStructure.getProprietaryProductFamily() != null)	{
			out.write(" proprietaryProductFamily=\"" + itemStructure.getProprietaryProductFamily() + "\"");
		}
		if (itemStructure.getCommodityCode() != null)	{
			out.write(" commodityCode=\"" + itemStructure.getCommodityCode() + "\"");
		}
		if (itemStructure.getManagedBy() != null)	{
			out.write(" managedBy=\"" + itemStructure.getManagedBy() + "\"");
		}
		if (itemStructure.getProductUnitOfMeasureCode() != null)	{
			out.write(" productUnitOfMeasureCode=\"" + itemStructure.getProductUnitOfMeasureCode() + "\"");
		}
		if (itemStructure.getMakeBuy() != null)	{
			out.write(" makeBuy=\"" + itemStructure.getMakeBuy() + "\"");
		}
		if (itemStructure.getMakeBuyOther() != null)	{
			out.write(" makeBuyOther=\"" + itemStructure.getMakeBuyOther() + "\"");
		}
		if (StringUtils.isNotBlank(itemStructure.getIsSerializationRequired()))	{
			out.write(" isSerializationRequired=\"" + itemStructure.getIsSerializationRequired() + "\"");
		}
		if (StringUtils.isNotBlank(itemStructure.getIsCertificationRequired()))	{
			out.write(" isCertificationRequired=\"" + itemStructure.getIsCertificationRequired() + "\"");
		}
		if (itemStructure.getOwnerName() != null)	{
			out.write(" ownerName=\"" + itemStructure.getOwnerName() + "\"");
		}
		if (itemStructure.getContactName() != null)	{
			out.write(" contactName=\"" + itemStructure.getContactName() + "\"");
		}
		if (itemStructure.getContactUniqueId() != null)	{
			out.write(" contactUniqueId=\"" + itemStructure.getContactUniqueId() + "\"");
		}
		if (StringUtils.isNoneBlank(itemStructure.getIsTopLevel()))	{
			out.write(" isTopLevel=\"" + itemStructure.getIsTopLevel() + "\"");
		}
		if (itemStructure.getBusinessEntity() != null)	{
			out.write(" businessEntity=\"" + itemStructure.getBusinessEntity() + "\"");
		}
		if (itemStructure.getBusinessEntityType() != null)	{
			out.write(" businessEntityType=\"" + itemStructure.getBusinessEntityType() + "\"");
		}
		if (itemStructure.getDataSource() != null)	{
			out.write(" dataSource=\"" + itemStructure.getDataSource() + "\"");
		}
		if (itemStructure.getEffectiveFromDate() != null)	{
			out.write(" effectiveFromDate=\"" + formatDate(itemStructure.getEffectiveFromDate()) + "\"");
		}
		if (itemStructure.getEffectiveToDate() != null)	{
			out.write(" effectiveToDate=\"" + formatDate(itemStructure.getEffectiveToDate()) + "\"");
		}
		if (itemStructure.getOperationCode() != null)	{
			out.write(" operationCode=\"" + itemStructure.getOperationCode() + "\"");
		}
		if (itemStructure.getLastUpdateDate() != null)	{
			out.write(" lastUpdateDate=\"" + formatDate(itemStructure.getLastUpdateDate()) + "\"");
		}
		out.write(">\n");
		
		
		node_Bom (itemStructure.getBom(), tabCount+1);
		node_AlternateItem (itemStructure.getAlternateItem(), tabCount+1);
		node_ApprovedVendorListItem (itemStructure.getApprovedVendorListItem(), tabCount+1);
		node_ApprovedManufacturerListItem (itemStructure.getApprovedManufacturerListItem(), tabCount+1);
		node_ItemPlatform (itemStructure.getItemPlatform(), tabCount+1);
		node_BuyerCode (itemStructure.getBuyerCode(), tabCount+1);
		node_Responsibility (itemStructure.getResponsibility(), tabCount+1);
		
		out.write(tabs(tabCount) + "</scplatform:Item>");
		out.write("\n");
	}

	protected String tabs(int count){
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < count; i++){
			tabs.append("\t");
		}
		return tabs.toString();
	}
	
	protected String formatString (String string)	{
		return string.replace("&", "&amp;").
				replace("'", "&apos;").
				replace("\"", "&quot;");
	}
	
	
	
	protected String getDateFormatString()	{
		if (dateFormatString == null){
			dateFormatString = Constants.DateFormatInbound;
		}
		return dateFormatString;
	}
	
	protected String formatDate (DateTime date){
		return date.toString(getDateFormatString());
	}
	
	protected void node_Bom( List<Bom> bom, int tabCounts) throws IOException {
		if (bom == null){
			return;
		}
		
		for (int index = 0; index < bom.size(); index++){
			out.write(tabs(tabCounts) + "<scplatform:Bom");
			
			if (bom.get(index).getBomName() != null)	{
				out.write(" bomName=\"" + formatString(bom.get(index).getBomName()) + "\"");
			}
			if (bom.get(index).getBomRevision() != null)	{
				out.write(" bomRevision=\"" + bom.get(index).getBomRevision() + "\"");
			}
			if (bom.get(index).getBomVersion() != null)	{
				out.write(" bomVersion=\"" + bom.get(index).getBomVersion() + "\"");
			}
			if (bom.get(index).getBomRevisionReleaseDate() != null)	{
				out.write(" bomRevisionReleaseDate=\"" + formatDate(bom.get(index).getBomRevisionReleaseDate()) + "\"");
			}
			if (bom.get(index).getBomVersionReleaseDate() != null)	{
				out.write(" bomVersionReleaseDate=\"" + formatDate(bom.get(index).getBomVersionReleaseDate()) + "\"");
			}
			if (bom.get(index).getDescription() != null)	{
				out.write(" description=\"" + formatString(bom.get(index).getDescription()) + "\"");
			}
			if (bom.get(index).getOwnerName() != null)	{
				out.write(" ownerName=\"" + bom.get(index).getOwnerName() + "\"");
			}
			if (bom.get(index).getOwnerContactUniqueIdentifier() != null)	{
				out.write(" ownerContactUniqueId=\"" + bom.get(index).getOwnerContactUniqueIdentifier() + "\"");
			}
			if (bom.get(index).getSite() != null)	{
				out.write(" site=\"" + bom.get(index).getSite() + "\"");
			}
//			if (bom.get(index).billOfMaterialTypeCode != null)	{
//				out.write(" billOfMaterialTypeCode=\"" + bom.get(index).billOfMaterialTypeCode.toString() + "\"");
//			}
//			if (bom.get(index).billOfMaterialTypeCodeOther != null)	{
//				out.write(" billOfMaterialTypeCodeOther=\"" + bom.get(index).billOfMaterialTypeCodeOther + "\"");
//			}
			if (StringUtils.isNotBlank(bom.get(index).getIsRepairs()))	{
				out.write(" isRepairs=\"" + bom.get(index).getIsRepairs() + "\"");
			}
			if (bom.get(index).getEffectiveFromDate() != null)	{
				out.write(" effectiveFromDate=\"" + formatDate(bom.get(index).getEffectiveFromDate()) + "\"");
			}
			if (bom.get(index).getEffectiveToDate() != null)	{
				out.write(" effectiveToDate=\"" + formatDate(bom.get(index).getEffectiveToDate()) + "\"");
			}
			if (bom.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + bom.get(index).getOperationCode() + "\"");
			}
			
			out.write(">\n");
			
			node_BomLine(bom.get(index).getBomLine(), tabCounts+1);
			
			out.write(tabs(tabCounts) + "</scplatform:Bom>");
			out.write("\n");
		}
		
	}

	
	protected void node_BomLine(List<BomLine> bomLine, int tabCounts) throws IOException {
		if (bomLine == null){
			return;
		}
		
		DecimalFormat numberFormat = new DecimalFormat("####.####");
		
		for (int index = 0; index < bomLine.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:BomLine");
			
			if (bomLine.get(index).getItemIdentifier() != null)	{
				out.write(" itemIdentifier=\"" + formatString(bomLine.get(index).getItemIdentifier()) + "\"");
			}
			if (bomLine.get(index).getItemUniqueId() != null)	{
				out.write(" itemUniqueId=\"" + formatString(bomLine.get(index).getItemUniqueId()) + "\"");
			}
			if (bomLine.get(index).getItemRevision() != null)	{
				out.write(" itemRevision=\"" + bomLine.get(index).getItemRevision() + "\"");
			}
			if (bomLine.get(index).getItemVersion() != null)	{
				out.write(" itemVersion=\"" + bomLine.get(index).getItemVersion() + "\"");
			}
			if (bomLine.get(index).getBusinessEntity() != null)	{
				out.write(" businessEntity=\"" + bomLine.get(index).getBusinessEntity() + "\"");
			}
			if (bomLine.get(index).getBusinessEntityType() != null)	{
				out.write(" businessEntityType=\"" + bomLine.get(index).getBusinessEntityType() + "\"");
			}
			if (bomLine.get(index).getManagedBy() != null)	{
				out.write(" managedBy=\"" + bomLine.get(index).getManagedBy() + "\"");
			}
//			if (StringUtils.isNotBlank(bomLine.get(index).getIsSerializationRequired()))	{
//				out.write(" isSerializationRequired=\"" + bomLine.get(index).getIsSerializationRequired() + "\"");
//			}
//			if (bomLine.get(index).billOfMaterialTypeCode != null)	{
//				out.write(" billOfMaterialTypeCode=\"" + bomLine.get(index).billOfMaterialTypeCode.toString() + "\"");
//			}
//			if (bomLine.get(index).billOfMaterialTypeCodeOther != null)	{
//				out.write(" billOfMaterialTypeCodeOther=\"" + bomLine.get(index).billOfMaterialTypeCodeOther + "\"");
//			}
			if (bomLine.get(index).getNotes() != null)	{
				out.write(" notes=\"" + bomLine.get(index).getNotes() + "\"");
			}
			if (bomLine.get(index).getItemQuantity() != NullValue.FLOAT)	{
				out.write(" itemQuantity=\"" + numberFormat.format(bomLine.get(index).getItemQuantity()) + "\"");
			}
			if (bomLine.get(index).getProductQuantityTypeCode() != null)	{
				out.write(" productQuantityTypeCode=\"" + bomLine.get(index).getProductQuantityTypeCode() + "\"");
			}
			if (bomLine.get(index).getProductQuantityTypeCodeOther() != null)	{
				out.write(" productQuantityTypeCodeOther=\"" + bomLine.get(index).getProductQuantityTypeCodeOther() + "\"");
			}
			if (bomLine.get(index).getDescription() != null)	{
				out.write(" description=\"" + formatString(bomLine.get(index).getDescription()) + "\"");
			}
			if (bomLine.get(index).getProprietarySequenceIdentifier() != NullValue.INTEGER)	{
				out.write(" proprietarySequenceIdentifier=\"" + bomLine.get(index).getProprietarySequenceIdentifier() + "\"");
			}
			if (bomLine.get(index).getEffectiveFromDate() != null)	{
				out.write(" effectiveFromDate=\"" + formatDate(bomLine.get(index).getEffectiveFromDate()) + "\"");
			}
			if (bomLine.get(index).getEffectiveToDate() != null)	{
				out.write(" effectiveToDate=\"" + formatDate(bomLine.get(index).getEffectiveToDate()) + "\"");
			}
			if (bomLine.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + bomLine.get(index).getOperationCode() + "\"");
			}

			out.write(">\n");
			
			node_ApprovedVendorListItem (bomLine.get(index).getApprovedVendorListItem(), tabCounts+1);
			node_ApprovedManufacturerListItem(bomLine.get(index).getApprovedManufacturerListItem(), tabCounts+1);

			out.write(tabs(tabCounts) + "</scplatform:BomLine>\n");
		}
		
	}

	
	protected void node_ApprovedVendorListItem(List<ApprovedVendorListItem> approvedVendorListItem, int tabCounts) throws IOException {
		if (approvedVendorListItem == null){
			return;
		}
		
		for (int index = 0; index < approvedVendorListItem.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:ApprovedVendorListItem");
			
			if (approvedVendorListItem.get(index).getSite() != null)	{
				out.write(" site=\"" + approvedVendorListItem.get(index).getSite() + "\"");
			}
			if (approvedVendorListItem.get(index).getDescription() != null)	{
				out.write(" description=\"" + formatString(approvedVendorListItem.get(index).getDescription()) + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorBusinessEntity() != null)	{
				out.write(" vendorBusinessEntity=\"" + approvedVendorListItem.get(index).getVendorBusinessEntity() + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorBusinessEntityType() != null)	{
				out.write(" vendorBusinessEntityType=\"" + approvedVendorListItem.get(index).getVendorBusinessEntityType() + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorItemIdentifier() != null)	{
				out.write(" vendorItemIdentifier=\"" + formatString(approvedVendorListItem.get(index).getVendorItemIdentifier()) + "\"");
			}			
			if (approvedVendorListItem.get(index).getVendorItemUniqueId() != null)	{
				out.write(" vendorItemUniqueId=\"" + formatString(approvedVendorListItem.get(index).getVendorItemUniqueId()) + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorRevision() != null)	{
				out.write(" vendorRevision=\"" + approvedVendorListItem.get(index).getVendorRevision() + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorVersion() != null)	{
				out.write(" vendorVersion=\"" + approvedVendorListItem.get(index).getVendorVersion() + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorContactName() != null)	{
				out.write(" vendorContactName=\"" + approvedVendorListItem.get(index).getVendorContactName() + "\"");
			}
			if (approvedVendorListItem.get(index).getVendorContactUniqueId() != null)	{
				out.write(" vendorContactUniqueId=\"" + approvedVendorListItem.get(index).getVendorContactUniqueId() + "\"");
			}
			if (approvedVendorListItem.get(index).getPartStatusCode() != null)	{
				out.write(" partStatusCode=\"" + approvedVendorListItem.get(index).getPartStatusCode() + "\"");
			}
			if (approvedVendorListItem.get(index).getPartStatusCodeOther() != null)	{
				out.write(" partStatusCodeOther=\"" + approvedVendorListItem.get(index).getPartStatusCodeOther() + "\"");
			}
			if (approvedVendorListItem.get(index).getPreferredStatusCode() != null)	{
				out.write(" preferredStatusCode=\"" + approvedVendorListItem.get(index).getPreferredStatusCode() + "\"");
			}
			if (approvedVendorListItem.get(index).getPreferredStatusStartDate() != null)	{
				out.write(" preferredStatusStartDate=\"" + formatDate(approvedVendorListItem.get(index).getPreferredStatusStartDate()) + "\"");
			}
			if (approvedVendorListItem.get(index).getPreferredStatusEndDate() != null)	{
				out.write(" preferredStatusEndDate=\"" + formatDate(approvedVendorListItem.get(index).getPreferredStatusEndDate()) + "\"");
			}
			if (approvedVendorListItem.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + approvedVendorListItem.get(index).getOperationCode() + "\"");
			}
			if (approvedVendorListItem.get(index).getLastUpdateDate() != null)	{
				out.write(" lastUpdateDate=\"" + formatDate(approvedVendorListItem.get(index).getLastUpdateDate()) + "\"");
			}

			out.write("/>\n");
		}
		
	}

	
	protected void node_ApprovedManufacturerListItem(List<ApprovedManufacturerListItem> approvedManufacturerListItem, int tabCounts) throws IOException {
		if (approvedManufacturerListItem == null){
			return;
		}
		
		
		for (int index = 0; index < approvedManufacturerListItem.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:ApprovedManufacturerListItem");
			
			if (approvedManufacturerListItem.get(index).getSite() != null)	{
				out.write(" site=\"" + approvedManufacturerListItem.get(index).getSite() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getDescription() != null)	{
				out.write(" description=\"" + formatString(approvedManufacturerListItem.get(index).getDescription()) + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerBusinessEntity() != null)	{
				out.write(" manufacturerBusinessEntity=\"" + approvedManufacturerListItem.get(index).getManufacturerBusinessEntity() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerBusinessEntityType() != null)	{
				out.write(" manufacturerBusinessEntityType=\"" + approvedManufacturerListItem.get(index).getManufacturerBusinessEntityType() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerItemIdentifier() != null)	{
				out.write(" manufacturerItemIdentifier=\"" + formatString(approvedManufacturerListItem.get(index).getManufacturerItemIdentifier()) + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerItemUniqueId() != null)	{
				out.write(" manufacturerItemUniqueId=\"" + formatString(approvedManufacturerListItem.get(index).getManufacturerItemUniqueId()) + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerRevision() != null)	{
				out.write(" manufacturerRevision=\"" + approvedManufacturerListItem.get(index).getManufacturerRevision() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerVersion() != null)	{
				out.write(" manufacturerVersion=\"" + approvedManufacturerListItem.get(index).getManufacturerVersion() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerContactName() != null)	{
				out.write(" manufacturerContactName=\"" + approvedManufacturerListItem.get(index).getManufacturerContactName() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getManufacturerContactUniqueId() != null)	{
				out.write(" manufacturerContactUniqueId=\"" + approvedManufacturerListItem.get(index).getManufacturerContactUniqueId() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getPartStatusCode() != null)	{
				out.write(" partStatusCode=\"" + approvedManufacturerListItem.get(index).getPartStatusCode() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getPartStatusCodeOther() != null)	{
				out.write(" partStatusCodeOther=\"" + approvedManufacturerListItem.get(index).getPartStatusCodeOther() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getPreferredStatusCode() != null)	{
				out.write(" preferredStatusCode=\"" + approvedManufacturerListItem.get(index).getPreferredStatusCode() + "\"");
			}
			if (approvedManufacturerListItem.get(index).getPreferredStatusStartDate() != null)	{
				out.write(" preferredStatusStartDate=\"" + formatDate(approvedManufacturerListItem.get(index).getPreferredStatusStartDate()) + "\"");
			}
			if (approvedManufacturerListItem.get(index).getPreferredStatusEndDate() != null)	{
				out.write(" preferredStatusEndDate=\"" + formatDate(approvedManufacturerListItem.get(index).getPreferredStatusEndDate()) + "\"");
			}
			if (approvedManufacturerListItem.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + approvedManufacturerListItem.get(index).getOperationCode() + "\"");
			}
			out.write("/>\n");
		}
		
	}
	
	
	protected void node_AlternateItem(List<AlternateItem> alternateItem, int tabCounts) throws IOException {
		if (alternateItem == null){
			return;
		}
		
		for (int index = 0; index < alternateItem.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:AlternateItem");
			
			if (alternateItem.get(index).getAltItemIdentifier() != null)	{
				out.write(" altItemIdentifier=\"" + formatString(alternateItem.get(index).getAltItemIdentifier()) + "\"");
			}
			if (alternateItem.get(index).getAltItemUniqueId() != null)	{
				out.write(" altItemUniqueId=\"" + formatString(alternateItem.get(index).getAltItemUniqueId()) + "\"");
			}
			if (alternateItem.get(index).getAltRevision() != null)	{
				out.write(" altRevision=\"" + alternateItem.get(index).getAltRevision() + "\"");
			}
			if (alternateItem.get(index).getAltVersion() != null)	{
				out.write(" altVersion=\"" + alternateItem.get(index).getAltVersion() + "\"");
			}
			if (alternateItem.get(index).getPreferredStatusCode() != null)	{
				out.write(" preferredStatusCode=\"" + alternateItem.get(index).getPreferredStatusCode() + "\"");
			}
			if (alternateItem.get(index).getPreferredStatusStartDate() != null)	{
				out.write(" preferredStatusStartDate=\"" + formatDate(alternateItem.get(index).getPreferredStatusStartDate()) + "\"");
			}
			if (alternateItem.get(index).getPreferredStatusEndDate() != null)	{
				out.write(" preferredStatusEndDate=\"" + formatDate(alternateItem.get(index).getPreferredStatusEndDate()) + "\"");
			}
			
			out.write("/>\n");
		}
	}

	protected void node_ItemPlatform(List<ItemPlatform> itemPlatform, int tabCounts) throws IOException {
		if (itemPlatform == null){
			return;
		}
		
		for (int index = 0; index < itemPlatform.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:ItemPlatform");
			
			if (itemPlatform.get(index).getPlatformName() != null)	{
				out.write(" platformName=\"" + itemPlatform.get(index).getPlatformName() + "\"");
			}
			if (itemPlatform.get(index).getDescription() != null)	{
				out.write(" description=\"" + formatString(itemPlatform.get(index).getDescription()) + "\"");
			}
			if (itemPlatform.get(index).getPlatformType() != null)	{
				out.write(" platformType=\"" + itemPlatform.get(index).getPlatformType() + "\"");
			}
			if (itemPlatform.get(index).getEffectiveFromDate() != null)	{
				out.write(" effectiveFromDate=\"" + formatDate(itemPlatform.get(index).getEffectiveFromDate()) + "\"");
			}
			if (itemPlatform.get(index).getEffectiveToDate() != null)	{
				out.write(" effectiveToDate=\"" + formatDate(itemPlatform.get(index).getEffectiveToDate()) + "\"");
			}
			if (itemPlatform.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + itemPlatform.get(index).getOperationCode().toString() + "\"");
			}

			out.write("/>\n");
		}
	}
	
	
	protected void node_BuyerCode(List<BuyerCode> buyerCode, int tabCounts) throws IOException {
		if (buyerCode == null){
			return;
		}
		
		for (int index = 0; index < buyerCode.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:BuyerCode");
			
			if (buyerCode.get(index).getBuyerCode() != null)	{
				out.write(" buyerCode=\"" + buyerCode.get(index).getBuyerCode() + "\"");
			}
			if (buyerCode.get(index).getBuyerCodeUserid() != null)	{
				out.write(" buyerCodeUserid=\"" + buyerCode.get(index).getBuyerCodeUserid() + "\"");
			}
			if (buyerCode.get(index).getSite() != null)	{
				out.write(" site=\"" + buyerCode.get(index).getSite() + "\"");
			}
			if (buyerCode.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + buyerCode.get(index).getOperationCode() + "\"");
			}

			out.write("/>\n");
		}
		
	}
	
	protected void node_Responsibility(List<Responsibility> responsibility, int tabCounts) throws IOException {
		if (responsibility == null){
			return;
		}
		
		for (int index = 0; index < responsibility.size(); index++)	{
			out.write(tabs(tabCounts) + "<scplatform:Responsibility");
			
			if (responsibility.get(index).getResponsibility() != null)	{
				out.write(" responsibility=\"" + responsibility.get(index).getResponsibility() + "\"");
			}
			if (responsibility.get(index).getResponsibilityUserId() != null)	{
				out.write(" responsibilityUserId=\"" + responsibility.get(index).getResponsibilityUserId() + "\"");
			}
			if (responsibility.get(index).getOperationCode() != null)	{
				out.write(" operationCode=\"" + responsibility.get(index).getOperationCode() + "\"");
			}
			
			out.write("/>\n");
		}
	}

	
}

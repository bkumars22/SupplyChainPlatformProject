/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.sourcingLane;

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
import com.test.selenium.common.StringUtilities;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecord;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordRange;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecordValue;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostValueDetail;
import com.google.common.collect.Lists;

/**
 * @author dgenrich
 *
 * @param <T>	SourcingLane or any class that extends it
 * 
 * @see MessageWriter
 */
public class SourcingLaneWriter<T extends SourcingLane> extends MessageWriter<T> {
	protected final String messageType = "SourcingLane";
	protected final String messageVersion = "MCM1.0";
	protected DecimalFormat numberFormat = new DecimalFormat("####.00");
	
	protected List<T> messageData;
	
	/**
	 * @param messageClazz	
	 * 		The SourcingLane Message class, typically SourcingLane.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link SourcingLaneBuilder}
	 */
	public SourcingLaneWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
	}

	@Override
	public String generate()	{
		
		try {
			openFile(saveToFile);
			buildHeader();
			
			for (int row = 0; row < messageData.size(); row++){
				buildSourcingLaneNode (messageData.get(row));
			}
			
			out.write("</scplatform:SourcingLaneMessage>\n");
			
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
		out.write("<scplatform:SourcingLaneMessage xmlns:scplatform=\"http://www.scplatform.local/E2openMCM\" ");
		out.write("headerVersion=\"1.0\" ");
		out.write("fromID=\"" + Constants.HubCompanyID + "\" ");
		out.write("toID=\"E2OPEN\" ");
		out.write("messageType=\"" + messageType + "\" ");
		out.write("messageVersion=\"" + messageVersion + "\" ");
		out.write("messageCount=\"0\" ");
		out.write("messageIndex=\"0\" ");
		out.write(">");
		out.write("\n");
	}
	
	protected void buildSourcingLaneNode(SourcingLane sourcingLaneStructure) throws IOException {
		buildSourcingLaneAttributes(sourcingLaneStructure);
		
		for (int row = 0; row < sourcingLaneStructure.getCostRecord().size(); row++){
			buildCostRecordNodes (sourcingLaneStructure.getCostRecord().get(row));
		}
		
		out.write("\t</scplatform:SourcingLane>\n");
	}
	
	
	protected void buildSourcingLaneAttributes(SourcingLane data) throws IOException {

		out.write("\t<scplatform:SourcingLane");
		
		if (data.getSourcingLaneIdentifier() != null){
			out.write(" sourcingLaneIdentifier=\"" + formatString(data.getSourcingLaneIdentifier()) + "\"");
		}
		if (data.getSourcingLaneExternalId() != null){
			out.write(" sourcingLaneExternalId=\"" + formatString(data.getSourcingLaneExternalId()) + "\"");
		}		
		if (data.getDescription() != null){
			out.write(" description=\"" + formatString(data.getDescription()) + "\"");
		}	
		if (data.getComment() != null){
			out.write(" comment=\"" + formatString(data.getComment()) + "\"");
		}	
		if (data.getItemIdentifier() != null){
			out.write(" itemIdentifier=\"" + formatString(data.getItemIdentifier()) + "\"");
		}	
		if (data.getItemUniqueId() != null){
			out.write(" itemUniqueId=\"" + formatString(data.getItemUniqueId()) + "\"");
		}	
		if (data.getItemRevision() != null){
			out.write(" itemRevision=\"" + data.getItemRevision() + "\"");
		}	
		if (data.getItemVersion() != null){
			out.write(" itemVersion=\"" + data.getItemVersion() + "\"");
		}	
		if (data.getBusinessEntity() != null){
			out.write(" businessEntity=\"" + data.getBusinessEntity() + "\"");
		}	
		if (data.getBusinessEntityType() != null){
			out.write(" businessEntityType=\"" + data.getBusinessEntityType() + "\"");
		}	
		
		// Site needs to be null for the allocation values to appear in the sourcing lane
//		if (data.site != null){
//			out.write(" site=\"" + data.site + "\"");
//		}	
		if (data.getFromBusinessEntity() != null){
			out.write(" fromBusinessEntity=\"" + data.getFromBusinessEntity() + "\"");
		}	
		if (data.getFromBusinessEntityType() != null){
			out.write(" fromBusinessEntityType=\"" + data.getFromBusinessEntityType() + "\"");
		}	
		if (data.getSite() != null){
			out.write(" site=\"" + data.getSite() + "\"");
		}	
		if (data.getFromSite() != null){
			out.write(" fromSite=\"" + data.getFromSite() + "\"");
		}	
		if (data.getLifeCycleCode() != null){
			out.write(" lifeCycleCode=\"" + data.getLifeCycleCode() + "\"");
		} else	{
			out.write(" lifeCycleCode=\"PRODUCTION\"");
		}
		if (data.getDateOffset() != NullValue.INTEGER){
			out.write(" dateOffset=\"" + data.getDateOffset() + "\"");
		}	
		if (data.getCurrencyCode() != null){
			out.write(" currencyCode=\"" + data.getCurrencyCode() + "\"");
		}	
		
		if (data.getState() != null){
			out.write(" state=\"" + data.getState() + "\"");
		} else	{
			out.write(" state=\"PENDING\"");
		}
		
		
		if (data.getOwnerName() != null){
			out.write(" ownerName=\"" + data.getOwnerName() + "\"");
		}	
		if (data.getOperationCode() != null){
			out.write(" operationCode=\"" + data.getOperationCode() + "\"");
		}	

		out.write(">");
		out.write("\n");
	}

	protected String formatString (String string)	{
		return string.replace("&", "&amp;").
				replace("'", "&apos;").
				replace("\"", "&quot;");
	}
	
	
	protected void buildCostRecordNodes(CostRecord data) throws IOException {
		out.write("\t\t<scplatform:CostRecord");
		
		if (data.getCostRecordExternalId() != null){
			out.write(" costRecordExternalId=\"" + formatString(data.getCostRecordExternalId()) + "\"");
		}
		if (data.getDescription() != null){
			out.write(" description=\"" + formatString(data.getDescription()) + "\"");
		}
		if (data.getComment() != null){
			out.write(" comment=\"" + formatString(data.getComment()) + "\"");
		}
		if (data.getCostType() != null){
			out.write(" costType=\"" + data.getCostType() + "\"");
		}
		if (data.getCostProviderBusinessEntity() != null){
			out.write(" costProviderBusinessEntity=\"" + data.getCostProviderBusinessEntity() + "\"");
		}
		if (data.getCostProviderBusinessEntityType() != null){
			out.write(" costProviderBusinessEntityType=\"" + data.getCostProviderBusinessEntityType() + "\"");
		}
		if (data.getEffectiveFromDate() != null){
			out.write(" effectiveFromDate=\"" + formatDate(data.getEffectiveFromDate()) + "\"");
		}
		if (data.getEffectiveToDate() != null){
			out.write(" effectiveToDate=\"" + formatDate(data.getEffectiveToDate()) + "\"");
		}
		if (data.getOperationCode() != null){
			out.write(" operationCode=\"" + data.getOperationCode() + "\"");
		}
		if (data.getPricingScenario() != null){
			out.write(" pricingScenario=\"" + data.getPricingScenario() + "\"");
		}
		if (data.getReasonCode() != null){
			out.write(" reasonCode=\"" + data.getReasonCode() + "\"");
		}
		out.write(">");
		out.write("\n");
		
		if (data.getCostRecordRange() != null){
			for (int row = 0; row < data.getCostRecordRange().size(); row++)	{
				buildCostRecordRange(data.getCostRecordRange().get(row));
			}			
		}

		if (data.getCostRecordValue() != null){
			for (int row = 0; row < data.getCostRecordValue().size(); row++)	{
				buildCostRecordValue(data.getCostRecordValue().get(row), "\t\t\t");
			}			
		}
		out.write("\t\t</scplatform:CostRecord>\n");
	}

	
	
	protected String dateFormatString = null;
	
	protected String getDateFormatString()	{
		if (dateFormatString == null){
			dateFormatString = Constants.DateFormatInbound;
		}
		return dateFormatString;
	}
	
	protected String formatDate (DateTime date){
		return date.toString(getDateFormatString());
	}
	
	
	protected void buildCostRecordRange(CostRecordRange data) throws IOException {
		DecimalFormat numberFormat = new DecimalFormat("####.##");
		
		out.write("\t\t\t<scplatform:CostRecordRange");
		
		if (data.getFromRange() != NullValue.FLOAT){
			out.write(" fromRange=\"" + numberFormat.format(data.getFromRange()) + "\"");
		}
		if (data.getToRange() != NullValue.FLOAT){
			out.write(" toRange=\"" + numberFormat.format(data.getToRange()) + "\"");
		}
		if (StringUtils.isNotBlank(data.getIsActive())){
			out.write(" isActive=\"" + data.getIsActive() + "\"");
		}
		out.write(">\n");
		
		for (int row = 0; row < data.getCostRecordValue().size(); row++)	{
			buildCostRecordValue(data.getCostRecordValue().get(row), "\t\t\t\t");
		}
		out.write("\t\t\t</scplatform:CostRecordRange>");
		out.write("\n");
		
	}




	private void buildCostRecordValue(CostRecordValue data, String tabs) throws IOException {
		if (
				(data.getCostElementType() == null) &&
				(data.getCostValue() == null) &&
				(data.getCostUnitofMeasureCode() == null)
				){
			// no data
			return;
		}

		out.write(tabs + "<scplatform:CostRecordValue");
		String cost = data.getCostValue();
		if (StringUtilities.isParsable(Float.class, cost))	{
			Float costValue = Float.parseFloat(cost);
			cost = numberFormat.format(costValue);
		}
		
		if (data.getCostElementType() != null){
			String costElement = data.getCostElementType(); 
			out.write(" costElementType=\"" + costElement + "\"");
		}
		if (data.getCostValue() != null){
			out.write(" costValue=\"" + cost + "\"");
		}
		if (data.getCostUnitofMeasureCode() != null){
			out.write(" costUnitOfMeasureCode=\"" + data.getCostUnitofMeasureCode() + "\"");
		}
		out.write(">\n");
		
		
		if (data.getCostValueDetail() != null){
			for (int row = 0; row < data.getCostValueDetail().size(); row++){
				buildCostValueDetail(data.getCostValueDetail().get(row), tabs + "\t");
			}
		}

		out.write(tabs + "</scplatform:CostRecordValue>");
		out.write("\n");
		
	}

	
	protected void buildCostValueDetail(CostValueDetail data, String tabs) throws IOException {
		if (
				(data.getCostValueName() == null) &&
				(data.getCostValueValue() == NullValue.FLOAT) &&
				(data.getCostValueBlend() == NullValue.FLOAT)
				){
			// no data
			return;
		}

		
		
		out.write(tabs + "<scplatform:CostValueDetail");
		
		if (data.getCostValueName() != null){
			out.write(" costValueName=\"" + data.getCostValueName() + "\"");
		}
		if (data.getCostValueValue() != NullValue.FLOAT){
			out.write(" costValueValue=\"" + numberFormat.format(data.getCostValueValue()) + "\"");
		}
		if (data.getCostValueBlend() != NullValue.FLOAT){
			out.write(" costValueBlend=\"" + numberFormat.format(data.getCostValueBlend()) + "\"");
		}

		out.write("/>\n");
	}
	
	
	
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.forecast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.StringUtilities;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;
import com.google.common.collect.Lists;

/**
 * @author dgenrich
 *
 * @param <T>	Forecast or any class that extends it
 * 
 * @see MessageWriter
 */
public class ForecastWriter<T extends Forecast> extends MessageWriter<T> {
	protected final String messageType = "Forecast";
	protected final String messageVersion = "MCM1.0";
	
	protected List<T> messageData;
	
	/**
	 * @param messageClazz	
	 * 		The Forecast Message class, typically Forecast.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link ForecastBuilder}
	 */
	public ForecastWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
	}

	@Override
	public String generate()	{
		
		try {
			openFile (saveToFile);
			buildHeader();
			
			for (int row = 0; row < messageData.size(); row++)	{
				buildForecastMessage(messageData.get(row));
			}
			
			out.write("</scplatform:ForecastMessage>");
			
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
		out.write("<scplatform:ForecastMessage xmlns:scplatform=\"http://www.scplatform.local/E2openMCM\" ");
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
	
	
	
	protected void buildForecastMessage(Forecast data) throws IOException {
		out.write("\t<scplatform:Forecast");
		
		writeAttribute("forecastExternalId", data.getForecastExternalId());
		writeAttribute("description", data.getDescription());
		writeAttribute("itemIdentifier", data.getItemIdentifier());
		writeAttribute("itemUniqueId", data.getItemUniqueId());
		writeAttribute("itemRevision", data.getItemRevision());
		writeAttribute("itemVersion", data.getItemVersion());
		writeAttribute("itemType", data.getItemType());
		writeAttribute("businessEntity", data.getBusinessEntity());
		writeAttribute("businessEntityType", data.getBusinessEntityType());
		writeAttribute("startDate", data.getStartDate());
		writeAttribute("endDate", data.getEndDate());
		writeAttribute("site", data.getSite());
		writeAttribute("forecastType", data.getForecastType());
		writeAttribute("forecastModel", data.getForecastModel());
		writeAttribute("lastChangeBy", data.getLastChangeBy());
		writeAttribute("bucketUnitOfMeasure", data.getBucketUnitOfMeasure());
		writeAttribute("calendarName", data.getCalendarName());
		writeAttribute("periodicAdjustmentValue", data.getPeriodicAdjustmentValue());
		writeAttribute("periodicAdjustmentType", data.getPeriodicAdjustmentType());
		writeAttribute("confidenceFactor", data.getConfidenceFactor());
		writeAttribute("remainingRolloverPeriods", data.getRemainingRolloverPeriods());
		writeAttribute("operationCode", data.getOperationCode());
		out.write(">\n");
		
		buildForecastBucket (data.getPointInTime());

		out.write("\t</scplatform:Forecast>");
		out.write("\n");
	}

		
	protected void buildForecastBucket(List<PointInTime> pointInTime) throws IOException {
		for (int row = 0; row < pointInTime.size(); row++){
			out.write("\t\t<scplatform:ForecastBucket ");
			writeAttribute ("startDate", pointInTime.get(row).getStartDate());
			writeAttribute ("endDate", pointInTime.get(row).getEndDate());
			writeAttribute ("period", pointInTime.get(row).getPeriod());
			writeAttribute ("pitTypeCode", pointInTime.get(row).getPitTypeCode());
			writeAttribute ("pitValue", MathUtils.format(pointInTime.get(row).getPitValue(), "####.00", RoundingMode.HALF_DOWN));
			writeAttribute ("periodicAdjustmentValue", pointInTime.get(row).getPeriodicAdjustmentValue());
			writeAttribute ("periodicAdjustmentType", pointInTime.get(row).getPeriodicAdjustmentType());
			writeAttribute ("operationCode", pointInTime.get(row).getOperationCode());
			out.write("/>\n");
		}
	}
	
	
	
	protected String formatString (String string)	{
		return string.replace("&", "&amp;").
				replace("'", "&apos;").
				replace("\"", "&quot;");
	}
	
	protected void writeAttribute (String attributeName, String data) throws IOException	{
		if (data != null)	{
			out.write(" " + attributeName + "=\"" + formatString(data) + "\"");
		}
	}

	
	protected void writeAttribute (String attributeName, DateTime data) throws IOException	{
		if (data != null)	{
			out.write(" " + attributeName + "=\"" + formatDate(data) + "\"");
		}
	}
	
	protected void writeAttribute (String attributeName, int data) throws IOException	{
		if (data != NullValue.INTEGER)	{
			out.write(" " + attributeName + "=\"" + data + "\"");
		}
	}
	
	protected void writeAttribute (String attributeName, float data) throws IOException	{
		if (data != NullValue.FLOAT)	{
			out.write(" " + attributeName + "=\"" + StringUtilities.formatNumber(data, "####.###") + "\"");
		}
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
}

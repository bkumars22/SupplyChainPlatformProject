/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.calendar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.constants.Constants;
import com.google.common.collect.Lists;

/**
 * @author dgenrich
 *
 * @param <T>	Calendar or any class that extends it
 * 
 * @see MessageWriter
 */
public class CalendarWriter<T extends Calendar> extends MessageWriter<T> {
	protected final String messageType = "Calendar";
	protected final String messageVersion = "MCM1.0";
	
	protected List<T> messageData;
	
	/**
	 * @param messageClazz	
	 * 		The Calendar Message class, typically Calendar.class, but can be any class that extends it.
	 * @param messageLines
	 * 		The message data, typically from {@link CalendarBuilder}
	 */
	public CalendarWriter(Class<T> messageClazz, Iterable<T> messageLines) {
		super(messageClazz, messageLines);
		messageData = Lists.newArrayList(messageLines);
	}

	@Override
	public String generate()	{
		
		try {
			openFile (saveToFile);
			
			buildCalendar();
			
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
	
	
	protected void buildCalendar() throws IOException	{
		ArrayList<String> calendars = new ArrayList<String>();
		ArrayList<String> quarters = new ArrayList<String>();
		ArrayList<String> months = new ArrayList<String>();
		ArrayList<String> weeks = new ArrayList<String>();
		boolean quarterEnded = true;
		boolean yearEnded = true;
		
		// build the header
		buildHeader();
		

		for (int row = 0; row < this.messageData.size(); row++)	{
			
			// handle starting Calendar Element
			if (!calendars.contains(this.messageData.get(row).getName()))	{
				if (!calendars.isEmpty())	{
					// if a calendar already written, then end Calendar Element
					out.write("\t\t\t</scplatform:Month>\n");
					out.write("\t\t</scplatform:Quarter>\n");
					out.write("\t</scplatform:Calendar>\n");
					yearEnded = true;
				}
				
				calendars.add(this.messageData.get(row).getName());
				startCalendar(this.messageData.get(row));
			} else	{
				yearEnded = false;
			}
			
			// handle starting Quarter
			if (!quarters.contains(this.messageData.get(row).getQuarter_Name()))	{
				if (!quarters.isEmpty() && !yearEnded)	{
					// if a quarters already written, then end Quarter Element
					out.write("\t\t\t</scplatform:Month>\n");
					out.write("\t\t</scplatform:Quarter>\n");
					
					quarterEnded = true;
				}
				
				quarters.add(this.messageData.get(row).getQuarter_Name());
				startQuarter(this.messageData.get(row));
			} else	{
				quarterEnded = false;
			}
			
			// handle starting Month
			if (!months.contains(this.messageData.get(row).getMonth_Name()))	{
				if (!months.isEmpty() && !quarterEnded && !yearEnded)	{
					// if a month already written, then end Month Element
					out.write("\t\t\t</scplatform:Month>\n");
				}
				
				months.add(this.messageData.get(row).getMonth_Name());
				startMonth(this.messageData.get(row));
			}
			
			// handle starting Week
			if (!weeks.contains(this.messageData.get(row).getWeek_Name()))	{
				weeks.add(this.messageData.get(row).getWeek_Name());
				startWeek(this.messageData.get(row));
			}
			
		}
		
		// end Last Month Element
		out.write("\t\t\t</scplatform:Month>\n");
		
		// end Last Quarter Element
		out.write("\t\t</scplatform:Quarter>\n");
		
		// end Last Calendar Element
		out.write("\t</scplatform:Calendar>\n");
		
		// end CalendarMessage node
		out.write("</scplatform:CalendarMessage>");
		
	}
	
	protected void buildHeader() throws IOException	{
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		
		out.write("<scplatform:CalendarMessage xmlns:scplatform=\"http://www.scplatform.local/E2openMCM\" ");
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

	protected void startCalendar(Calendar data) throws IOException{
		String tab = "\t";
		
		
		out.write(tab + "<scplatform:Calendar");
		if (data.getName() != null){
			out.write(" name=\"" + data.getName() + "\"");
		}
		if (data.getStartDate() != null){
			out.write(" startDate=\"" + formatDate(data.getStartDate()) + "\"");
		}
		if (data.getCalendarType() != null){
			out.write(" calendarType=\"" + data.getCalendarType() + "\"");
		}
		if (data.getDescription() != null){
			out.write(" description=\"" + data.getDescription() + "\"");
		}
		if (data.getOperationCode() != null){
			out.write(" operationCode=\"" + data.getOperationCode() + "\"");
		}
		
		out.write(">");
		out.write("\n");
	}

	protected void startQuarter(Calendar data) throws IOException{
		String tab = "\t\t";
		
		
		out.write(tab + "<scplatform:Quarter");
		if (data.getQuarter_Name() != null){
			out.write(" name=\"" + data.getQuarter_Name() + "\"");
		}
		if (data.getQuarter_StartDate() != null){
			out.write(" startDate=\"" + formatDate(data.getQuarter_StartDate()) + "\"");
		}
		if (data.getQuarter_EndDate() != null){
			out.write(" endDate=\"" + formatDate(data.getQuarter_EndDate()) + "\"");
		}
		if (data.getQuarter_FiscalQuarter() != NullValue.INTEGER){
			out.write(" fiscalQuarter=\"" + data.getQuarter_FiscalQuarter() + "\"");
		}

		out.write(">");
		out.write("\n");
	}
	
	protected void startMonth(Calendar data) throws IOException{
		String tab = "\t\t\t";
		
		
		out.write(tab + "<scplatform:Month");
		if (data.getMonth_Name() != null){
			out.write(" name=\"" + data.getMonth_Name() + "\"");
		}
		if (data.getMonth_FiscalMonth() != NullValue.INTEGER){
			out.write(" fiscalMonth=\"" + data.getMonth_FiscalMonth() + "\"");
		}
		if (data.getMonth_StartDate() != null){
			out.write(" startDate=\"" + formatDate(data.getMonth_StartDate()) + "\"");
		}
		if (data.getMonth_EndDate() != null){
			out.write(" endDate=\"" + formatDate(data.getMonth_EndDate()) + "\"");
		}

		out.write(">");
		out.write("\n");
	}
	
	protected void startWeek(Calendar data) throws IOException{
		String tab = "\t\t\t\t";
		
		
		out.write(tab + "<scplatform:Week");
		if (data.getWeek_Name() != null){
			out.write(" name=\"" + data.getWeek_Name() + "\"");
		}
		if (data.getWeek_FiscalWeek() != NullValue.INTEGER){
			out.write(" fiscalWeek=\"" + data.getWeek_FiscalWeek() + "\"");
		}
		if (data.getWeek_StartDate() != null){
			out.write(" startDate=\"" + formatDate(data.getWeek_StartDate()) + "\"");
		}
		if (data.getWeek_EndDate() != null){
			out.write(" endDate=\"" + formatDate(data.getWeek_EndDate()) + "\"");
		}

		out.write("/>");
		out.write("\n");
	}
	
	protected String dateFormatString = null;
	
	protected String getDateFormatString()	{
		if (dateFormatString == null){
			dateFormatString = Constants.DateFormatInbound;
		}
		return dateFormatString;
	}
	
	protected String formatDate (DateTime date){
		
//		DateFormat formattor = new SimpleDateFormat(getDateFormatString());
//		return formattor.format(date);
		return date.toString(getDateFormatString());
	}
	
}

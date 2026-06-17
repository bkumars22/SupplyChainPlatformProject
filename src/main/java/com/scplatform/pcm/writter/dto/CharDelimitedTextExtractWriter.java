/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.writter.dto;

import com.scplatform.pcm.searchframework.dto.GenericResultRow;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Provides a basic tab delimited extract writer.
 * Suitable for most excel extracts
 * It uses the columns from the query to generate the extract.
 */
public class CharDelimitedTextExtractWriter extends AbstractExtractWriter
{
	public static final String DEFAULT_ROW_DELIMITER = "\n";
	public static final String DEFAULT_COLUMN_DELIMITER = "\t";

	protected String colDelimiter;
	protected String rowDelimiter;
			
	protected NumberFormat numberFormat = new DecimalFormat("0.0#####");			 	
	protected NumberFormat percentFormat = new DecimalFormat("0.00%");
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
	public CharDelimitedTextExtractWriter()
	{
		super();
		colDelimiter = DEFAULT_COLUMN_DELIMITER;
		rowDelimiter = DEFAULT_ROW_DELIMITER;
		
	}
	
	public CharDelimitedTextExtractWriter(String colDelimiter, String rowDelimiter)
	{
		super();
		this.colDelimiter = colDelimiter;
		this.rowDelimiter = rowDelimiter;
	}

	protected void writeCol(StringBuilder line, Date value)
	{
		if (value != null)
		{
			line.append(dateFormat.format(value));
		}
		line.append(colDelimiter);		
	}

	protected void writeCol(StringBuilder line, Number value)
	{
		if (value != null)
		{
			line.append(numberFormat.format(value));
		}
		line.append(colDelimiter);		
	}

	protected void writeCol(StringBuilder line, BigDecimal value)
	{
		if (value != null)
		{
			line.append(numberFormat.format(value));
		}
		line.append(colDelimiter);
	}

	protected void writeCol(StringBuilder line, Object value)
	{
		if (value != null)
		{
			line.append(HtmlUtils.htmlEscape(value.toString()));
		}
		line.append(colDelimiter);
	}
	
	public boolean onRow(GenericResultRow row)
	{
		StringBuilder line = new StringBuilder(500);	
		
		List<Object>colValues = row.getValues();

        for (Object col: colValues)
        {
	       	writeCol(line,col);
        }
        line.append(rowDelimiter);
	    try
		{
			writer.append(line);
		}
		catch (IOException e)
		{
			logger.warn("OnRow failed",e);
		}
        charsWritten += line.length();
        rowsWritten++;
		
		return true;
	}

	public void start(String[] columnNames)
	{
		StringBuilder line = new StringBuilder(500);
		
        for (String name : columnNames)
        {
        	line.append(name).append(colDelimiter);
        }
        line.append(rowDelimiter);
	    try
		{
			writer.append(line);
		}
		catch (IOException e)
		{
			logger.warn("Start failed",e);			
		}	
        charsWritten += line.length();			
	}

	public void end(String[] columnNames)
	{
		
	}

	public String getColDelimiter()
	{
		return colDelimiter;
	}

	public String getRowDelimiter()
	{
		return rowDelimiter;
	}
}
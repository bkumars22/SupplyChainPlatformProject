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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Map;
/**
 * Helper class that can be subclassed for creating useful builders	
 * @see CharDelimitedTextExtractWriter	
 */
public abstract class AbstractExtractWriter implements ExtractWriter
{
	protected final static Logger logger = LogManager.getLogger(AbstractExtractWriter.class);
	protected Writer writer;
	protected int charsWritten = 0;
	protected int rowsWritten = 0;
	
	public AbstractExtractWriter()
	{
		super();
		charsWritten = 0;
		rowsWritten = 0;		
	}

	public void setStream(OutputStream os, String encoding) 
		throws UnsupportedEncodingException
	{
		this.writer = new OutputStreamWriter(os,encoding);
	}

	public int getCharsWritten()
	{
		return charsWritten;
	}
	
	public int getRowsWritten()
	{
		return rowsWritten;
	}

	public boolean close()
	{
        try
        {
        	writer.close();
        	return true;
        }
        catch(IOException e)
        {
        	logger.warn("Unable to close writer",e);
        	return false;
		}        	
	}
	
	public void inititalize(Map<String,Object> data)
	{
	}
	
}
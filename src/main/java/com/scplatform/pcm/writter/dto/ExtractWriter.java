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


import com.scplatform.pcm.searchframework.service.SearchQueryBuilder;
import com.scplatform.pcm.searchframework.service.SearchQueryResultCallback;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Extents SearchQueryResultCallback interface
 * which has the row data interfaces
 * This adds the stream behaviors.  Since the SearchAction
 */
public interface ExtractWriter extends SearchQueryResultCallback
{
	public abstract void inititalize(Map<String,Object> data);
	
	public abstract void setStream(OutputStream os, String encoding)
		throws UnsupportedEncodingException;

	public abstract int getCharsWritten();
	
	public abstract int getRowsWritten();
	
	
	public abstract boolean close();

}
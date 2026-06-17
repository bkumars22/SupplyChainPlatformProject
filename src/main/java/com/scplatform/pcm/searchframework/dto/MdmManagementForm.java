/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.searchframework.dto;

import java.text.SimpleDateFormat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;

/**
 * Base for some of the MDM related forms.
 */
@SuppressWarnings("serial")
@Log4j2
public class MdmManagementForm extends SearchForm
{

	protected UiMessages validationErrors;
	protected SimpleDateFormat sdf;
	protected int maxRowsForUpdate = -1;
	protected String[] managedFlagOptions = null;
	PcmConfigUtil pcmConfigUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
	public void setMaxRowsForMassUpdate(int maxRows)
	{
		maxRowsForUpdate = maxRows;
	}

	public int getMaxRowsForMassUpdate()
	{
		return maxRowsForUpdate;
	}

	public String[] getManagedFlagOptions()
	{
		return managedFlagOptions;
	}

	public void reset( HttpServletRequest request)
	{
		super.reset(request);		
		if (sdf == null)
		{			
			try
			{
				sdf = AppContextHelper.getValidContext(request).getDateFormatter();				
			}
			catch (InvalidUserContext e)
			{
				log.warn("Unable initialize data parser", e);
			}
		}		
		if (maxRowsForUpdate < 0)
		{
			maxRowsForUpdate = pcmConfigUtil.getIntValue("mdm.maxRowsForMassChange",1000);
		}
		if (managedFlagOptions == null)
		{
			managedFlagOptions = pcmConfigUtil.getList("pcm.managedFlags").toArray(new String[0]);
		}
		
		validationErrors = new UiMessages();		
	}
}
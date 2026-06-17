/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a list initializer based on either a string of values 
 * or a property key that is looked up.
 * If a string, it should be in the format of [a,b,d] or [a=alabel,b=blabel,d=dlabel]
 * 
 */
@Service
@RequiredArgsConstructor
public class SearchListInitializer implements SearchParameterInitializer
{
	private static Log logger = LogFactory.getLog(SearchListInitializer.class);	
	Map<String,String> values = new HashMap<String,String>();
    private final PcmConfigUtil pcmConfig;

	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			if (values != null)
			{
				for (Map.Entry<String,String> value: values.entrySet())
				{
					list.addSelectValue(value.getValue(), value.getKey());
				}
			}
			return true;
		}		
		return false;
	}

	public void setInitialData(String data)
	{
		if (data != null)
		{
			values.clear();
			if (data.startsWith("[") && data.endsWith("]"))
			{
				data = StringUtils.substringBetween(data, "[", "]");
			}
			else
			{
				try
				{
					if (pcmConfig.containsKey(data))
					{
						data = pcmConfig.getString(data);
						if (data.startsWith("[") && data.endsWith("]"))
						{
							data = StringUtils.substringBetween(data, "[", "]");
						}						
					}
				}
				catch (Exception e)
				{
					logger.warn("Unable to intialize search list",e);
				}
			}
			String[] parts = data.split(",");
			for (String part: parts)
			{
				String[]pair = part.split("=");
				if (pair != null)
				{
					if (pair.length > 1)
					{						
						values.put(StringUtils.trim(pair[0]), StringUtils.trim(pair[1]));
					}
					else
					{
						values.put(StringUtils.trim(pair[0]), StringUtils.trim(pair[0]));
					}
				}				
			}
		}
	}
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;


import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CostRecordExceptionLOBListInitializer implements SearchParameterInitializer
{
	private final PcmConfigUtil pcmConfigUtil;

	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			List<String> configuredLOB = pcmConfigUtil.getList("pcm.costrecord.exception.allowableLOB", new ArrayList<String>());
			
			for (String LOB: configuredLOB)
			{
				list.addSelectValue(LOB,LOB);
			}
			
			return true;
		}
		else
		{
			return false;
		}
	}

	public void setInitialData(String data)
	{
	}
}

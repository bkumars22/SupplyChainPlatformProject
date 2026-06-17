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
public class GenericItemInitializer implements SearchParameterInitializer
{
    private final PcmConfigUtil pcmConfigUtil;

	public boolean initializeParameter(SearchParameter parameter, Map context)
	{
		List<String> genericItem = pcmConfigUtil.getList("pcm.item.flexattribute.usertItemType.genericTypes", new ArrayList<String>());
		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			genericItem.stream ().forEach(item -> list.addSelectValue(item, item));
			 return true;
		} else {
			return false;
        }
	}

	@Override
	public void setInitialData (String data) {}
}

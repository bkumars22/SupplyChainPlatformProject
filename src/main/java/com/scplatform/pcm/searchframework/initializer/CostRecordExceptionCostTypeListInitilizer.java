/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.repository.PcmCostTypeRepository;
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
public class CostRecordExceptionCostTypeListInitilizer implements SearchParameterInitializer {

	private final PcmCostTypeRepository costTypeModule;
    private final PcmConfigUtil pcmConfigUtil;

	@Override
	public boolean initializeParameter(SearchParameter parameter, Map context) {

		if (parameter instanceof SearchParameterSelect)
		{
			SearchParameterSelect list = (SearchParameterSelect)parameter;
			List<String> configuredCostTypes = pcmConfigUtil.getList("pcm.costrecord.exception.allowableCostTypes", new ArrayList<String>());
			
			for (PcmCostType type: costTypeModule.getAllCostTypes())
			{
				if(configuredCostTypes.contains(type.getCostTypeName())) {
					list.addSelectValue(type.getCostTypeName(), type.getCostTypeKey());
				}
			}
			
			return true;
		}
		else
		{
			return false;
		}
	
	}

	@Override
	public void setInitialData(String data) {
		// TODO Auto-generated method stub
		
	}

}

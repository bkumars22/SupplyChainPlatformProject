/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.service.PcmCostTypeService;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CostTypeListInitializer implements SearchParameterInitializer {

    private final PcmCostTypeService costTypeService;

    @Override
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (parameter instanceof SearchParameterSelect) {
            SearchParameterSelect list = (SearchParameterSelect) parameter;
            for (PcmCostType type : costTypeService.getAllCostTypes()) {
                list.addSelectValue(type.getCostTypeName(), type.getCostTypeKey());
            }
            return true;
        }
        return false;
    }

    @Override
    public void setInitialData(String data) {
    }
}


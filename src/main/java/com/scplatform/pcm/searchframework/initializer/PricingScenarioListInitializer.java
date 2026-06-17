/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import com.scplatform.pcm.cost.entity.PcmPricingScenario;
import com.scplatform.pcm.cost.repository.PcmPricingScenarioRepository;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchParameterInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PricingScenarioListInitializer implements SearchParameterInitializer {

    private final PcmPricingScenarioRepository repository;

    @Override
    public boolean initializeParameter(SearchParameter parameter, Map context) {
        if (parameter instanceof SearchParameterSelect) {
            SearchParameterSelect list = (SearchParameterSelect) parameter;
            for (PcmPricingScenario type : repository.getAllPricingScenarios()) {
                list.addSelectValue(type.getPricingScenarioName(), type.getPricingScenarioKey().toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public void setInitialData(String data) {
    }
}


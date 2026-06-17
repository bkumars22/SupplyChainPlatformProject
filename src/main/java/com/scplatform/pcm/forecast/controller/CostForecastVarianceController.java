/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;


import com.scplatform.pcm.forecast.dto.CostForecastVarianceForm;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Properties;

@Controller
@RequiredArgsConstructor
public class CostForecastVarianceController {

    private final SearchService searchService;
    private static final String VIEW_COST_FORECAST_VARIANCE_SEARCH = "costForecastVarianceSearchPage";
    private static final String FORM_ATTR         = "costForecastVarianceForm";
    private static final String SEARCH_DEFINITION = "SearchDefCostForecastVariance.xml";
    private static final String SEARCH_ACTION     = "costForecastVarianceSearch";
    private static final String ENABLE_PAGING     = "false";

    private Properties buildSearchProperties() {
        Properties properties = new Properties();
        properties.put("definition",   SEARCH_DEFINITION);
        properties.put("searchAction", SEARCH_ACTION);
        properties.put("enablePaging", ENABLE_PAGING);
        return properties;
    }

    private void exposeViewAttributes(Model model, CostForecastVarianceForm form) {
        model.addAttribute(FORM_ATTR,      form);
        model.addAttribute("definition",   SEARCH_DEFINITION);
        model.addAttribute("searchAction", SEARCH_ACTION);
        model.addAttribute("enablePaging", ENABLE_PAGING);
    }

    @RequestMapping("/costForecastVariance")
    public String init(CostForecastVarianceForm form,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) throws Exception {
        Properties properties = buildSearchProperties();
        searchService.init(properties, form, request, response);
        exposeViewAttributes(model, form);
        return VIEW_COST_FORECAST_VARIANCE_SEARCH;
    }
}


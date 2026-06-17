/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;


import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.service.PcmForecastService;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PcmSearchForecastController {


    private final SearchService searchService;
    private final PcmConfigUtil pcmConfigUtil;
    private final PcmForecastService pcmForecastService;
    private static final String VIEW_FORECAST_SEARCH = "forecastSearchPage";
    private static final String VIEW_FORECAST_PAGE   = "forecastPage";
    private static final String FORM_ATTR         = "forecastSearchForm";
    private static final String SEARCH_DEFINITION = "SearchDefForecast.xml";
    private static final String FORECAST_TYPE     = "COST";
    private void applyConfiguredDefaults(ForecastForm form) {
        form.setShowAllColumns(
                pcmConfigUtil.getBoolean("pcm.forecastdetails.expanded", true));
    }
    
    private void applyFixedPresets(ForecastForm form) {
        if (form == null) {
            return;
        }
        form.setPresetValue("forecastType", FORECAST_TYPE);
    }

    private Properties buildSearchProperties() {
        Properties properties = new Properties();
        properties.put("definition",   SEARCH_DEFINITION);
        properties.put("forecastType", FORECAST_TYPE);
        return properties;
    }

    private void exposeViewAttributes(Model model, ForecastForm form) {
        model.addAttribute(FORM_ATTR,      form);
        model.addAttribute("definition",   SEARCH_DEFINITION);
        model.addAttribute("forecastType", FORECAST_TYPE);
    }

    @RequestMapping("/searchForecast")
    public String init(ForecastForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        applyConfiguredDefaults(form);
        Properties properties = buildSearchProperties();
        searchService.init(properties, form, request, response);
            applyFixedPresets(form);
        exposeViewAttributes(model, form);
        return VIEW_FORECAST_SEARCH;
    }

    @RequestMapping("/submitForecastSearch")
    public String search(ForecastForm form, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            applyConfiguredDefaults(form);
            Properties properties = buildSearchProperties();
            form = searchService.mergeRequestWithCachedForm(form, request);
            applyFixedPresets(form);
            searchService.search(properties, form, request, response);
            exposeViewAttributes(model, form);
        } catch (Exception ex) {
            log.error("submitForecastSearch failed", ex);
            if (!model.containsAttribute(FORM_ATTR)) {
                exposeViewAttributes(model, form);
            }
            model.addAttribute("searchError", ex.getMessage());
        }
        return VIEW_FORECAST_SEARCH;
    }

    @RequestMapping("/setForecastKeys")
    public String setForecastKeys(ForecastForm form, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            form = searchService.mergeRequestWithCachedForm(form, request);
            searchService.setSelectedKeys(buildSearchProperties(), form, request);
            List<Long> forecastKeys = new ArrayList<>();
            if (form.getSelectedKeys() != null) {
                for (String key : form.getSelectedKeys()) {
                    if (key == null || key.isEmpty()) {
                        continue;
                    }
                    try {
                        forecastKeys.add(Long.valueOf(key));
                    } catch (NumberFormatException nfe) {
                        log.warn("Skipping non-numeric forecast key: {}", key);
                    }
                }
            }

            form.clearForecastRecords();
            if (!forecastKeys.isEmpty()) {
                List<PcmForecast> forecasts = pcmForecastService.findAllByIds(forecastKeys);
                form.addForecastRecords(forecasts);
            }
            model.addAttribute("forecastForm", form);
            model.addAttribute(FORM_ATTR, form);
        } catch (Exception ex) {
            log.error("setForecastKeys failed", ex);
            exposeViewAttributes(model, form);
            model.addAttribute("searchError", ex.getMessage());
            return VIEW_FORECAST_SEARCH;
        }
        return VIEW_FORECAST_PAGE;
    }
}

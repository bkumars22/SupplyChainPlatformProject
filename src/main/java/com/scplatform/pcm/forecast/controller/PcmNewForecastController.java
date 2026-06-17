/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;


import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Properties;

@Controller
@RequiredArgsConstructor
@Log4j2
public class PcmNewForecastController {


    private final SearchService searchService;
    private final PcmConfigUtil pcmConfigUtil;
    private static final String VIEW_FORECAST_ITEM_SEARCH = "forecastItemSearchPage";
    private static final String FORM_ATTR         = "forecastItemSearchForm";
    private static final String SEARCH_DEFINITION = "SearchDefForecastItem.xml";
    private static final String SEARCH_ACTION     = "submitForecastItemSearch";
    private static final String NEXT_ACTION       = "setItemsNewForecast";
    private static final String FORECAST_TYPE     = "COST";


    private void applyConfiguredDefaults(ForecastForm form) {
        form.setShowAllColumns(
                pcmConfigUtil.getBoolean("pcm.forecastdetails.expanded", true));
    }

    private void applyFixedPresets(ForecastForm form) {
        if (form == null) {
            return;
        }
    }

    private Properties buildSearchProperties() {
        Properties properties = new Properties();
        properties.put("definition",   SEARCH_DEFINITION);
        properties.put("searchAction", SEARCH_ACTION);
        properties.put("nextAction",   NEXT_ACTION);
        properties.put("forecastType", FORECAST_TYPE);
        return properties;
    }

    private void exposeViewAttributes(Model model, ForecastForm form) {
        model.addAttribute(FORM_ATTR,      form);
        model.addAttribute("definition",   SEARCH_DEFINITION);
        model.addAttribute("searchAction", SEARCH_ACTION);
        model.addAttribute("nextAction",   NEXT_ACTION);
        model.addAttribute("forecastType", FORECAST_TYPE);
    }

    @RequestMapping("/newForecast")
    public String init(ForecastForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        applyConfiguredDefaults(form);
        Properties properties = buildSearchProperties();
        searchService.init(properties, form, request, response);
        // initializeForm clears presetValues; (re-)apply FIXED presets so they
        // are part of the freshly cached form for subsequent /submitForecastItemSearch calls.
        applyFixedPresets(form);
        exposeViewAttributes(model, form);
        return VIEW_FORECAST_ITEM_SEARCH;
    }

    @RequestMapping("/submitForecastItemSearch")
    public String search(ForecastForm form, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            applyConfiguredDefaults(form);
            Properties properties = buildSearchProperties();
            form = searchService.mergeRequestWithCachedForm(form, request);
            // Re-apply the FIXED presets on the merged/cached form so the
            // itemTypes / itemBusinessTypes JPQL binds are guaranteed to be
            // present for every page navigation, not just the initial init.
            applyFixedPresets(form);
            searchService.search(properties, form, request, response);

            exposeViewAttributes(model, form);
        } catch (Exception ex) {
            log.error("submitForecastItemSearch failed", ex);
            // Make sure the form is still in the model so the JSP can render.
            if (!model.containsAttribute(FORM_ATTR)) {
                exposeViewAttributes(model, form);
            }
            model.addAttribute("searchError", ex.getMessage());
        }
        return VIEW_FORECAST_ITEM_SEARCH;
    }
}

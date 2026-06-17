/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.dto.SupplyAllocationExceptionForm;
import com.scplatform.pcm.tam.dto.TAMHistoryForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Properties;

@Controller
@Log4j2
@RequiredArgsConstructor
public class TamExceptionController {

    private final SearchService searchService;

    private static final String VIEW_TAM_EXCEPTION_SEARCH = "/tam/tamException";

    @RequestMapping("/supplyAllocationException")
    public String init(SupplyAllocationExceptionForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        log.debug("TamExceptionController init start");
        Properties properties = new Properties();
        properties.put("definition", "SearchDefTAMException.xml");
        properties.put("enablePaging", "true");

        model.addAttribute("tamExceptionForm", form);
        searchService.init(properties, form, request, response);
        log.debug("TamExceptionController init end");
        return VIEW_TAM_EXCEPTION_SEARCH;
    }

    @RequestMapping("/submitSupplyAllocationException")
    public String search(SupplyAllocationExceptionForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("TamExceptionController search start");
        Properties properties = new Properties();
        properties.put("enablePaging", "true");

        form = searchService.mergeRequestWithCachedForm(form, request);
        form.setMessagePopup(null);
        model.addAttribute("tamExceptionForm", form);
        searchService.search(properties, form, request, response);
        long endTime = System.currentTimeMillis();
        log.debug("TamExceptionController search end, time taken: " + (endTime - startTime) + "ms");
        return VIEW_TAM_EXCEPTION_SEARCH;
    }

}

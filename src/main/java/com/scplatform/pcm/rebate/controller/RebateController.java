/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.rebate.controller;

import com.scplatform.pcm.rebate.dto.RebateProgramSearchForm;
import com.scplatform.pcm.searchframework.service.SearchService;
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
public class RebateController {

    private final SearchService searchService;

    private static final String VIEW_REBATE_SEARCH = "/rebate/rebateSearchPage";

    private static final String REBATE_FORM_NAME = "rebateSearchForm";


    @RequestMapping("/searchRebateProgram")
    public String init(RebateProgramSearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start to initialize rebate program search page.");
        Properties properties = new Properties();
        properties.put("definition", "SearchDefRebateProgram.xml");
        model.addAttribute(REBATE_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("Finished initializing rebate program search page. Time taken: {} ms", System.currentTimeMillis() - startTime);
        return VIEW_REBATE_SEARCH;
    }

    @RequestMapping("/submitRebateProgramSearch")
    public String search(RebateProgramSearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start to search rebate program.");
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(REBATE_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("Finished searching rebate program. Time taken: {} ms", System.currentTimeMillis() - startTime);
        return VIEW_REBATE_SEARCH;
    }
}

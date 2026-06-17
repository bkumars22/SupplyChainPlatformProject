/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
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
public class TAMAuditHistoryController {

    private final SearchService searchService;

    private static final String VIEW_TAM_AUDIT_HISTORY_SEARCH = "/tam/supplyAllocationAuditHistory";

    @RequestMapping("/supplyAllocationHistory")
    public String init(TAMHistoryForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSupplyAllocationHistory.xml");
        model.addAttribute("tamHistoryForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_TAM_AUDIT_HISTORY_SEARCH;
    }

    @RequestMapping("/submitSupplyAllocationHistory")
    public String search(TAMHistoryForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("tamHistoryForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_TAM_AUDIT_HISTORY_SEARCH;
    }
}

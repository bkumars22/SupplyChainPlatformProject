/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.controller;

import com.scplatform.pcm.searchframework.dto.SearchForm;
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
@RequiredArgsConstructor
@Log4j2
public class AuditHistoryController {

    private final SearchService searchService;

    private static final String VIEW_AUDIT_HISTORY_SEARCH = "auditHistoryPage";


    @RequestMapping("/auditHistorySearch")
    public String init(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefAuditHistory.xml");
        model.addAttribute("auditHistoryForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_AUDIT_HISTORY_SEARCH;
    }

    @RequestMapping("/submitAuditHistorySearch")
    public String search(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("auditHistoryForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_AUDIT_HISTORY_SEARCH;
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.controller;

import com.scplatform.pcm.businessEntity.dto.BusinessAdminForm;
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
public class BusinessEntityController {

    private final SearchService searchService;


    private static final String VIEW_MANAGE_BUSINESS_SEARCH = "/admin/businessAdmin";


    @RequestMapping("/searchBusiness")
    public String init(BusinessAdminForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefBusiness.xml");
        model.addAttribute("businessAdminForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_MANAGE_BUSINESS_SEARCH;
    }

    @RequestMapping("/submitBusinessSearch")
    public String search(BusinessAdminForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("businessAdminForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_MANAGE_BUSINESS_SEARCH;
    }
}

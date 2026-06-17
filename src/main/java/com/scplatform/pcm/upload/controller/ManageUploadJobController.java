/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.controller;

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
public class ManageUploadJobController {

    private final SearchService searchService;

    private static final String FORM_ATTRIBUTE = "loadJobAdminForm";
    private static final String VIEW_LOAD_JOB_SEARCH = "loadJobSearchPage";

    @RequestMapping("/loadJobSearch")
    public String init(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefLoadJob.xml");
        properties.put("searchAction", "submitLoadJobSearch");
        model.addAttribute(FORM_ATTRIBUTE, form);
        searchService.init(properties, form, request, response);
        return VIEW_LOAD_JOB_SEARCH;
    }

    @RequestMapping("/submitLoadJobSearch")
    public String search(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(FORM_ATTRIBUTE, form);
        searchService.search(properties, form, request, response);
        return VIEW_LOAD_JOB_SEARCH;
    }
}


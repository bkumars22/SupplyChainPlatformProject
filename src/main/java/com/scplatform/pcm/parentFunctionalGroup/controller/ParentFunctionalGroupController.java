/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.parentFunctionalGroup.controller;

import com.scplatform.pcm.functionalGroup.dto.FunctionalGroupForm;
import com.scplatform.pcm.parentFunctionalGroup.dto.ParentFunctionalGroupForm;
import com.scplatform.pcm.searchframework.dto.SearchDefinition;
import com.scplatform.pcm.searchframework.dto.SearchExpression;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Properties;

@Service
@Log4j2
@RequiredArgsConstructor
public class ParentFunctionalGroupController {

    private final SearchService searchService;

    private static final String VIEW_FUNCTIONAL_GROUP_SEARCH = "/tam/parentFunctionalGroupManage";


    @RequestMapping("/manageParent")
    public String init(ParentFunctionalGroupForm form, HttpServletRequest request, HttpServletResponse response,
                       Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefParentFunctionalGroup.xml");
        model.addAttribute("parentFunctionalGroupForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_FUNCTIONAL_GROUP_SEARCH;
    }

    @RequestMapping("/searchParentGroup")
    public String search(ParentFunctionalGroupForm form, HttpServletRequest request, HttpServletResponse response,
                         Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("parentFunctionalGroupForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_FUNCTIONAL_GROUP_SEARCH;
    }

}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.contact.controller;

import com.scplatform.pcm.contact.dto.ContactAdminForm;
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
public class ContactController {

    private final SearchService searchService;

    private static final String VIEW_CONTACT_ADMIN_SEARCH = "/admin/contactAdmin";

    private static final String CONTACT_ADMIN_FORM_NAME = "contactAdminForm";


    @RequestMapping("/searchContact")
    public String init(ContactAdminForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start to initialize contact admin search page.");
        Properties properties = new Properties();
        properties.put("definition", "SearchDefContact.xml");
        model.addAttribute(CONTACT_ADMIN_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("Finished initializing contact admin search page. Time taken: {} ms", System.currentTimeMillis() - startTime);
        return VIEW_CONTACT_ADMIN_SEARCH;
    }

    @RequestMapping("/submitContactSearch")
    public String search(ContactAdminForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("Start to contact admin program.");
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(CONTACT_ADMIN_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("Finished searching contact admin Time taken: {} ms", System.currentTimeMillis() - startTime);
        return VIEW_CONTACT_ADMIN_SEARCH;
    }
}

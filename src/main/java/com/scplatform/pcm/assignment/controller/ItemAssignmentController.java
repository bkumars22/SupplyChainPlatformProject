/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.assignment.controller;

import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.assignment.dto.ItemManagementForm;
import com.scplatform.pcm.assignment.service.ItemManagemntService;
import com.scplatform.pcm.searchframework.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ItemAssignmentController {

    private final SearchService searchService;
        private final ItemManagemntService itemManagemntService;
    private static final String VIEW_ITEM_ASSIGNMENT = "/mdm/itemManagementPage";

    @RequestMapping("/startItemManagement")
    public String initItemAssignemnt(ItemManagementForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /startItemManagement - initItemAssignemnt at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefItemMgmt.xml");
        form.setSiteList(itemManagemntService.getSiteList());
        model.addAttribute("itemManagementForm", form);
        searchService.init(properties, form, request, response);
        log.info("END /startItemManagement - initItemAssignemnt at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_ITEM_ASSIGNMENT;
    }

    @RequestMapping("/submitItemManagementSearch")
    public String submitItemAssignemntSearch(ItemManagementForm form, HttpServletRequest request,
            HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitItemManagementSearch - submitItemAssignemntSearch at {}", startTime);
        Properties properties = new Properties();
        form = (ItemManagementForm) searchService.mergeRequestWithCachedForm(form, request);
        form.setSiteList(itemManagemntService.getSiteList());
        model.addAttribute("itemManagementForm", form);
        searchService.search(properties, form, request, response);
        log.info("END /submitItemManagementSearch - submitItemAssignemntSearch at {}, took {} ms",
                System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_ITEM_ASSIGNMENT;
}
}

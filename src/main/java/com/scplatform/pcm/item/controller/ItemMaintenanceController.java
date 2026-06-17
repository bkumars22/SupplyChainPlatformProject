/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

import com.scplatform.pcm.item.dto.ItemMaintenanceForm;
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
public class ItemMaintenanceController {

    private final SearchService searchService;

    private static final String VIEW_ITEM_MAINTENANCE_SEARCH = "/admin/itemMaintenance";


    @RequestMapping("/searchItemMaintenance")
    public String init(ItemMaintenanceForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefItemMaintenance.xml");
        model.addAttribute("itemMaintenanceForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_ITEM_MAINTENANCE_SEARCH;
    }

    @RequestMapping("/submitItemMaintenanceSearch")
    public String search(ItemMaintenanceForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("itemMaintenanceForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_ITEM_MAINTENANCE_SEARCH;
    }
}

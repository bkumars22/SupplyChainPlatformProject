/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.supplyAllocation.controller;

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
public class SupplyAllocationController {


    private final SearchService searchService;

    private static final String VIEW_SUPPLY_ALLOCATION_SEARCH = "supplyAllocationSearchPage";
    private static final String VIEW_SUPPLY_ALLOCATION_ITEM_SEARCH = "supplyAllocationItemSearchPage";
    private static final String VIEW_SUPPLY_ALLOCATION_PAGE = "supplyAllocationPage";

    @RequestMapping("/searchSupplyAllocation")
    public String init(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSupplyAllocation.xml");
        properties.put("searchAction", "submitSupplyAllocationSearch");
        model.addAttribute("supplyAllocationSearchForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_SUPPLY_ALLOCATION_SEARCH;
    }

    @RequestMapping("/submitSupplyAllocationSearch")
    public String search(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        //properties.put("definition", "SearchDefSupplyAllocation.xml");
        //properties.put("searchAction", "submitSupplyAllocationSearch");
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("supplyAllocationSearchForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_SUPPLY_ALLOCATION_SEARCH;
    }

    @RequestMapping("/createItemSupplyAllocation")
    public String initItemSearch(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSupplyAllocationItem.xml");
        model.addAttribute("supplyAllocationItemSearchForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_SUPPLY_ALLOCATION_ITEM_SEARCH;
    }

    @RequestMapping("/submitSupplyAllocationItemSearch")
    public String searchItem(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSupplyAllocationItem.xml");
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("supplyAllocationItemSearchForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_SUPPLY_ALLOCATION_ITEM_SEARCH;
    }
}


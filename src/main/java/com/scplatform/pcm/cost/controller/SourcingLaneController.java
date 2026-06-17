/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.controller;

import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.cost.dto.SourcingLaneForm;
import com.scplatform.pcm.searchframework.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class SourcingLaneController {

    private static final String VIEW_SOURCING_LANE_ITEM_SEARCH = "/pricing/sourcingLaneItemSearchPage";
    private static final String VIEW_SOURCING_LANE_SEARCH = "/pricing/sourcingLaneSearchPage";
    private static final String SOURCING_LANE_FORM_NAME = "sourcingLaneForm";
    private static final String SOURCING_LANE_SEARCH_FORM_NAME = "sourcingLaneSearchForm";

    private final SearchService searchService;

    @RequestMapping("/newSourcingLane")
    public String initItemSearch(SourcingLaneForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /newSourcingLane - initItemSearch at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSourcingLaneItem.xml");
        model.addAttribute(SOURCING_LANE_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /newSourcingLane - initItemSearch at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_SOURCING_LANE_ITEM_SEARCH;
    }

    @RequestMapping("/submitSourcingLaneItemSearch")
    public String searchItem(SourcingLaneForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitSourcingLaneItemSearch - searchItem at {}", startTime);
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(SOURCING_LANE_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("END /submitSourcingLaneItemSearch - searchItem at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_SOURCING_LANE_ITEM_SEARCH;
    }

    @RequestMapping("/searchSourcingLane")
    public String initSearch(SourcingLaneForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /searchSourcingLane - initSearch at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefSourcingLane.xml");
        model.addAttribute(SOURCING_LANE_SEARCH_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /searchSourcingLane - initSearch at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_SOURCING_LANE_SEARCH;
    }

    @RequestMapping("/submitSourcingLaneSearch")
    public String search(SourcingLaneForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitSourcingLaneSearch - search at {}", startTime);
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(SOURCING_LANE_SEARCH_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("END /submitSourcingLaneSearch - search at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_SOURCING_LANE_SEARCH;
    }
}
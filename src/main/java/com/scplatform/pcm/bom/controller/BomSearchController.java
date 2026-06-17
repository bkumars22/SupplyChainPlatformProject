/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.controller;

import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.bom.dto.BomManagementForm;
import com.scplatform.pcm.bom.dto.BomSearchForm;
import com.scplatform.pcm.bom.service.BomService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class BomSearchController {

    private static final String VIEW_BOM_SEARCH = "/search/bomSearchPage";
    private static final String BOM_SEARCH_FORM_NAME = "bomSearchForm";
    private static final String VIEW_BOM_MANAGEMENT_SEARCH = "/mdm/bomManagementSearchPage";
    private static final String BOM_MANAGEMENT_FORM_NAME = "bomManagementForm";

    private final SearchService searchService;
    private final BomService bomService;

    @RequestMapping("/bomSearch" )
    public String initBomSearch(BomSearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /bomSearch - initBomSearch at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefBom.xml");
        model.addAttribute(BOM_SEARCH_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /bomSearch - initBomSearch at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_BOM_SEARCH;
    }

    @RequestMapping("/submitBomSearch")
    public String searchBom(BomSearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitBomSearch - searchBom at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefBom.xml");
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(BOM_SEARCH_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("END /submitBomSearch - searchBom at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_BOM_SEARCH;
    }

    @RequestMapping("/startBomManagement")
    public String initBomManagement(BomManagementForm form, HttpServletRequest request, HttpServletResponse response,
            Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /startBomManagement - initBomManagement at {}", startTime);
        form.setAttritionDefectTypesMap(bomService.getDefectTypeMap());
        form.clearLineMessages();
        Properties properties = new Properties();
        properties.put("definition", "SearchDefBomMgmt.xml");
        model.addAttribute(BOM_MANAGEMENT_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /startBomManagement - initBomManagement at {}, took {} ms", System.currentTimeMillis(),
                System.currentTimeMillis() - startTime);
        return VIEW_BOM_MANAGEMENT_SEARCH;
    }

    @RequestMapping("/submitBomManagementSearch")
    public String searchBomManagement(BomManagementForm form, HttpServletRequest request,
            HttpServletResponse response, Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitBomManagementSearch - searchBomManagement at {}", startTime);
        try {
            Properties properties = new Properties();
            form = (BomManagementForm) searchService.mergeRequestWithCachedForm(form, request);
            form.setAttritionDefectTypesMap(bomService.getDefectTypeMap());
            model.addAttribute(BOM_MANAGEMENT_FORM_NAME, form);
            searchService.search(properties, form, request, response);
        } catch (Throwable t) {
            log.error("Errors in /submitBomManagementSearch", t);
        }
        log.info("END /submitBomManagementSearch - searchBomManagement at {}, took {} ms",
                System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_BOM_MANAGEMENT_SEARCH;
    }
}

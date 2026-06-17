/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.controller;

import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollup;
import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollupForm;
import com.scplatform.pcm.bomCostRollUp.service.BomCostRollupService;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;


@Controller
@RequiredArgsConstructor
@Log4j2
public class BomCostRollupController {

    private static final String VIEW_BOM_COST_ROLLUP_SEARCH = "bomCostRollupSearchPage";
    private static final String FORM_ATTR                   = "bomCostRollupForm";

    private final SearchService searchService;
    private final BomCostRollupService bomCostRollupService;

    // ------------------------------------------------------------------
    // Search page – init / submit
    // ------------------------------------------------------------------

    @RequestMapping("/startBomCostRollupManagement")
    public String init(BomCostRollupForm form,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefBomCostRollUp.xml");
        searchService.init(properties, form, request, response);
        model.addAttribute(FORM_ATTR, form);
        log.info("Return the init page for BOM Cost Rollup Management");
        return VIEW_BOM_COST_ROLLUP_SEARCH;
    }

    @RequestMapping("/submitBomCostRollupManagement")
    public String search(BomCostRollupForm form,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) throws Exception {
        form = searchService.mergeRequestWithCachedForm(form, request);
        // ACL check + populate visible cost-element columns
        bomCostRollupService.prepareSearchForm(form, request);
        searchService.search(new Properties(), form, request, response);
        model.addAttribute(FORM_ATTR, form);
        log.info("Return the search result page for BOM Cost Rollup Management");
        return VIEW_BOM_COST_ROLLUP_SEARCH;
    }

    @GetMapping("/mcm/api/bomCostRollup/viewBomCostRollup")
    public @ResponseBody ResponseEntity<BomCostRollup> viewBomCostRollup(
            @RequestParam("bomKey") long bomKey,
            HttpServletRequest request) {
        try {
            BomCostRollup payload = bomCostRollupService.getBomRollupData(bomKey, request);
            return new ResponseEntity<>(payload, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error processing BOM Rollup AJAX request for bomKey={}", bomKey, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

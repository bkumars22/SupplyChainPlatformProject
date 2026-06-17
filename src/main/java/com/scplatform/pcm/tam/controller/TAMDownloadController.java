/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.dto.*;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.dto.TAMDownloadForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@Log4j2
@RequiredArgsConstructor
public class TAMDownloadController {

    private final SearchService searchService;

    private static final String VIEW_TAM_DOWNLOAD_SEARCH = "/tam/tamMultipleDownload";

    private final static String noSupplierHQLQuery = "{VALUE} (select 1 from TAMAllocation tam inner join tam.supplierAllocations tsa " +
            "where tam.functionalGroup = fg and tam.site = site and tsa.endDate >= trunc(current_date())" +
            " and tsa.allocation > 0 and tsa.businessEntity = be)";


    @RequestMapping("/searchTAMDownload")
    public String init(TAMDownloadForm form, HttpServletRequest request, HttpServletResponse response,
                       Model model) throws Exception {
        log.debug("Inside init method of TAMDownloadController");
        Properties properties = new Properties();
        properties.put("definition", "SearchDefTAMDownload.xml");
        model.addAttribute("tamDownloadForm", form);
        searchService.init(properties, form, request, response);
        log.debug("Exiting init method of TAMDownloadController");
        return VIEW_TAM_DOWNLOAD_SEARCH;
    }

    @RequestMapping("/searchTAMMultipleDownload")
    public String search(TAMDownloadForm form, HttpServletRequest request, HttpServletResponse response,
                         Model model) throws Exception {
        long startTime = System.currentTimeMillis();
        log.debug("Inside search method of TAMDownloadController");
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("tamDownloadForm", form);
        searchService.search(properties, form, request, response);
        form.setGlobalRegionCheck(false);
        long startTimeForSupplierCount = System.currentTimeMillis();
        searchService.setSupplierCountForTAMDownload(form, noSupplierHQLQuery);
        long endTimeForSupplierCount = System.currentTimeMillis();
        log.debug("Time taken to set supplier count for TAM Download: " + (endTimeForSupplierCount - startTimeForSupplierCount) + " ms");
        searchService.setItemCountForTAMDownload(form, noSupplierHQLQuery);
        long endTimeForItemCount = System.currentTimeMillis();
        log.debug("Time taken to set item count for TAM Download: " + (endTimeForItemCount - endTimeForSupplierCount) + " ms");
        log.debug("Exiting search method of TAMDownloadController. Total time taken: " + (endTimeForItemCount - startTime) + " ms");
        return VIEW_TAM_DOWNLOAD_SEARCH;
    }
}

/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.commodityProfile.controller;

import com.scplatform.pcm.commodityProfile.dto.CommodityProfileForm;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.ums.dto.GenericResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Properties;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CommodityProfileController {

    private final SearchService searchService;
    private final CommodityProfileService commodityProfileService;

    private static final String VIEW_COMMODITY_PROFILE_SEARCH = "commodityProfileSearchPage";
    

   @RequestMapping("/commodityProfileSearch")
    public String init(CommodityProfileForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefCommodityProfile.xml");
        model.addAttribute("commodityProfileSearchForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_COMMODITY_PROFILE_SEARCH;
    }

    @RequestMapping("/submitCommodityProfileSearch")
    public String search(CommodityProfileForm form, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("commodityProfileSearchForm", form);
        searchService.search(properties, form, request, response);
        return VIEW_COMMODITY_PROFILE_SEARCH;
    }

    @DeleteMapping("mcm/api/commodityprofile/delete")
    public @ResponseBody ResponseEntity<GenericResponse> removeCommodityProfile(
            @RequestBody List<String> profileIds,
            HttpServletRequest request) throws Exception {

        commodityProfileService.deleteCommodityProfileByUserKey(profileIds);

        return new ResponseEntity<GenericResponse>(HttpStatus.OK);
    }
}

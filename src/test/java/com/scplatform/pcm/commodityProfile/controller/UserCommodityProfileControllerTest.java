/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import com.scplatform.pcm.commodityProfile.dto.UserCommodityProfileForm;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.ums.dto.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class UserCommodityProfileControllerTest {

    @Mock private SearchService searchService;
    @Mock private CommodityProfileService commodityProfileService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private UserCommodityProfileController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_setsDefinitionAndReturnsView() throws Exception {
        UserCommodityProfileForm form = new UserCommodityProfileForm();
        String view = controller.init(form, request, response, model);
        assertEquals("userCommodityProfileMappingSearchPage", view);
        verify(model).addAttribute("userCommodityProfileMappingForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefUserCommodityProfileMapping.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesAndCallsSearch() throws Exception {
        UserCommodityProfileForm form = new UserCommodityProfileForm();
        UserCommodityProfileForm merged = new UserCommodityProfileForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("userCommodityProfileMappingSearchPage", view);
        verify(model).addAttribute("userCommodityProfileMappingForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }

    @Test
    void removeCommodityProfile_delegatesToService() throws Exception {
        List<String> keys = Arrays.asList("1~Prof~3");
        ResponseEntity<GenericResponse> result = controller.removeCommodityProfile(keys, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(commodityProfileService).deleteCommodityProfileMapping(keys);
    }
}

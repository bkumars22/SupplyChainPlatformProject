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

import com.scplatform.pcm.commodityProfile.dto.CommodityProfileForm;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.ums.dto.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CommodityProfileControllerTest {

    @Mock private SearchService searchService;
    @Mock private CommodityProfileService commodityProfileService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private CommodityProfileController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_setsDefinitionAndReturnsSearchPage() throws Exception {
        CommodityProfileForm form = new CommodityProfileForm();
        String view = controller.init(form, request, response, model);
        assertEquals("commodityProfileSearchPage", view);
        verify(model).addAttribute("commodityProfileSearchForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefCommodityProfile.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesAndCallsSearch() throws Exception {
        CommodityProfileForm form = new CommodityProfileForm();
        CommodityProfileForm merged = new CommodityProfileForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("commodityProfileSearchPage", view);
        verify(model).addAttribute("commodityProfileSearchForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }

    @Test
    void removeCommodityProfile_delegatesToServiceAndReturnsOk() throws Exception {
        List<String> ids = Arrays.asList("1~ProfA", "2~ProfB");
        ResponseEntity<GenericResponse> result = controller.removeCommodityProfile(ids, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(commodityProfileService).deleteCommodityProfileByUserKey(ids);
    }
}

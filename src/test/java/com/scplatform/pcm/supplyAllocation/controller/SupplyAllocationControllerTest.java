/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.supplyAllocation.controller;

import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SupplyAllocationControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private SupplyAllocationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_setsDefinitionAndReturnsSearchPage() throws Exception {
        SearchForm form = new SearchForm();
        String view = controller.init(form, request, response, model);
        assertEquals("supplyAllocationSearchPage", view);
        verify(model).addAttribute("supplyAllocationSearchForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefSupplyAllocation.xml", cap.getValue().get("definition"));
        assertEquals("submitSupplyAllocationSearch", cap.getValue().get("searchAction"));
    }

    @Test
    void search_mergesAndCallsSearch() throws Exception {
        SearchForm form = new SearchForm();
        SearchForm merged = new SearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);
        String view = controller.search(form, request, response, model);
        assertEquals("supplyAllocationSearchPage", view);
        verify(searchService).mergeRequestWithCachedForm(form, request);
        verify(model).addAttribute("supplyAllocationSearchForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }

    @Test
    void initItemSearch_setsItemDefinition_returnsItemView() throws Exception {
        SearchForm form = new SearchForm();
        String view = controller.initItemSearch(form, request, response, model);
        assertEquals("supplyAllocationItemSearchPage", view);
        verify(model).addAttribute("supplyAllocationItemSearchForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefSupplyAllocationItem.xml", cap.getValue().get("definition"));
    }

    @Test
    void searchItem_mergesAndCallsSearch_withItemDefinition() throws Exception {
        SearchForm form = new SearchForm();
        SearchForm merged = new SearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);
        String view = controller.searchItem(form, request, response, model);
        assertEquals("supplyAllocationItemSearchPage", view);
        verify(model).addAttribute("supplyAllocationItemSearchForm", merged);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).search(cap.capture(), eq(merged), eq(request), eq(response));
        assertEquals("SearchDefSupplyAllocationItem.xml", cap.getValue().get("definition"));
    }
}

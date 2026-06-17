/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import com.scplatform.pcm.bom.dto.BomSearchForm;
import com.scplatform.pcm.bom.service.BomService;
import com.scplatform.pcm.searchframework.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class BomSearchControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private BomService bomService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    private BomSearchController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BomSearchController(searchService, bomService);
    }

    @Test
    void initBomSearch_addsFormAndCallsInitWithDefinition() throws Exception {
        BomSearchForm form = new BomSearchForm();

        String view = controller.initBomSearch(form, request, response, model);

        assertEquals("/search/bomSearchPage", view);
        verify(model).addAttribute("bomSearchForm", form);

        ArgumentCaptor<Properties> props = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(props.capture(), any(BomSearchForm.class), any(), any());
        assertEquals("SearchDefBom.xml", props.getValue().get("definition"));
    }

    @Test
    void searchBom_mergesFormAndCallsSearch() throws Exception {
        BomSearchForm form = new BomSearchForm();
        BomSearchForm merged = new BomSearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.searchBom(form, request, response, model);

        assertEquals("/search/bomSearchPage", view);
        verify(searchService, times(1)).mergeRequestWithCachedForm(form, request);
        verify(model).addAttribute("bomSearchForm", merged);

        ArgumentCaptor<Properties> props = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).search(props.capture(), any(BomSearchForm.class), any(), any());
        assertEquals("SearchDefBom.xml", props.getValue().get("definition"));
    }
}

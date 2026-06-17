/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.dto.SupplyAllocationExceptionForm;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TamExceptionControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private TamExceptionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_returnsExceptionView_andSetsDefinition() throws Exception {
        SupplyAllocationExceptionForm form = new SupplyAllocationExceptionForm();

        String view = controller.init(form, request, response, model);

        assertEquals("/tam/tamException", view);
        verify(model).addAttribute("tamExceptionForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefTAMException.xml", cap.getValue().get("definition"));
        assertEquals("true", cap.getValue().get("enablePaging"));
    }

    @Test
    void search_mergesAndSearches_clearsMessagePopup() throws Exception {
        SupplyAllocationExceptionForm form = new SupplyAllocationExceptionForm();
        SupplyAllocationExceptionForm merged = new SupplyAllocationExceptionForm();
        merged.setMessagePopup("some popup");
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("/tam/tamException", view);
        verify(searchService).mergeRequestWithCachedForm(form, request);
        // messagePopup must be cleared
        assertNull(merged.getMessagePopup());
        verify(model).addAttribute("tamExceptionForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }
}

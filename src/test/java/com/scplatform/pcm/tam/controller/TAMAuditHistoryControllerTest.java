/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.dto.TAMHistoryForm;
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

class TAMAuditHistoryControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private TAMAuditHistoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_returnsAuditHistoryView_andCallsSearchInit() throws Exception {
        TAMHistoryForm form = new TAMHistoryForm();

        String view = controller.init(form, request, response, model);

        assertEquals("/tam/supplyAllocationAuditHistory", view);
        verify(model).addAttribute("tamHistoryForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefSupplyAllocationHistory.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesFormAndSearches_returnsAuditHistoryView() throws Exception {
        TAMHistoryForm form = new TAMHistoryForm();
        TAMHistoryForm merged = new TAMHistoryForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("/tam/supplyAllocationAuditHistory", view);
        verify(searchService).mergeRequestWithCachedForm(form, request);
        verify(model).addAttribute("tamHistoryForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }
}

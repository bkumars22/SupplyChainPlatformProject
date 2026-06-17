/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.controller;

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

class AuditHistoryControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private AuditHistoryController controller;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void init_returnsAuditHistoryPage_andCallsSearchInit() throws Exception {
        SearchForm form = new SearchForm();
        String view = controller.init(form, request, response, model);
        assertEquals("auditHistoryPage", view);
        verify(model).addAttribute("auditHistoryForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefAuditHistory.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesAndSearches() throws Exception {
        SearchForm form = new SearchForm();
        SearchForm merged = new SearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);
        String view = controller.search(form, request, response, model);
        assertEquals("auditHistoryPage", view);
        verify(model).addAttribute("auditHistoryForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }
}

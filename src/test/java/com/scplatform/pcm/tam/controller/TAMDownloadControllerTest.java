/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.tam.dto.TAMDownloadForm;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TAMDownloadControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private TAMDownloadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_returnsDownloadView_andSetsDefinition() throws Exception {
        TAMDownloadForm form = new TAMDownloadForm();

        String view = controller.init(form, request, response, model);

        assertEquals("/tam/tamMultipleDownload", view);
        verify(model).addAttribute("tamDownloadForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefTAMDownload.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesFormAndSearches_setsCountsAndReturnsView() throws Exception {
        TAMDownloadForm form = new TAMDownloadForm();
        TAMDownloadForm merged = new TAMDownloadForm();
        merged.setGlobalRegionCheck(true); // will be reset to false by controller
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("/tam/tamMultipleDownload", view);
        verify(searchService).mergeRequestWithCachedForm(form, request);
        verify(model).addAttribute("tamDownloadForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
        // globalRegionCheck must be reset to false
        assertEquals(Boolean.FALSE, merged.getGlobalRegionCheck());
        // supplier and item count calls
        verify(searchService).setSupplierCountForTAMDownload(eq(merged), anyString());
        verify(searchService).setItemCountForTAMDownload(eq(merged), anyString());
    }
}

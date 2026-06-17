/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.controller;

import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollup;
import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollupForm;
import com.scplatform.pcm.bomCostRollUp.service.BomCostRollupService;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BomCostRollupControllerTest {

    @Mock private SearchService searchService;
    @Mock private BomCostRollupService bomCostRollupService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;
    @InjectMocks private BomCostRollupController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void init_setsDefinition_andReturnsSearchPage() throws Exception {
        BomCostRollupForm form = new BomCostRollupForm();
        String view = controller.init(form, request, response, model);
        assertEquals("bomCostRollupSearchPage", view);
        verify(model).addAttribute("bomCostRollupForm", form);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefBomCostRollUp.xml", cap.getValue().get("definition"));
    }

    @Test
    void search_mergesForm_callsPrepareAndSearch_returnsSearchPage() throws Exception {
        BomCostRollupForm form = new BomCostRollupForm();
        BomCostRollupForm merged = new BomCostRollupForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);
        String view = controller.search(form, request, response, model);
        assertEquals("bomCostRollupSearchPage", view);
        verify(bomCostRollupService).prepareSearchForm(merged, request);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
        verify(model).addAttribute("bomCostRollupForm", merged);
    }

    @Test
    void viewBomCostRollup_returnsOk_withPayload() throws Exception {
        BomCostRollup payload = new BomCostRollup();
        when(bomCostRollupService.getBomRollupData(42L, request)).thenReturn(payload);
        ResponseEntity<BomCostRollup> resp = controller.viewBomCostRollup(42L, request);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(payload, resp.getBody());
    }

    @Test
    void viewBomCostRollup_returnsInternalServerError_onException() throws Exception {
        when(bomCostRollupService.getBomRollupData(42L, request)).thenThrow(new RuntimeException("boom"));
        ResponseEntity<BomCostRollup> resp = controller.viewBomCostRollup(42L, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }
}

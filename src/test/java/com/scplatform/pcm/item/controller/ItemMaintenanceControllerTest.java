/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

import com.scplatform.pcm.item.dto.ItemMaintenanceForm;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemMaintenanceControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    @InjectMocks
    private ItemMaintenanceController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInit_PassesDefinitionAndReturnsView() throws Exception {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        String view = controller.init(form, request, response, model);

        assertEquals("/admin/itemMaintenance", view);
        verify(model).addAttribute("itemMaintenanceForm", form);

        ArgumentCaptor<Properties> propCap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(propCap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefItemMaintenance.xml", propCap.getValue().get("definition"));
    }

    @Test
    void testSearch_DelegatesToSearchService() throws Exception {
        ItemMaintenanceForm form = new ItemMaintenanceForm();
        ItemMaintenanceForm merged = new ItemMaintenanceForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("/admin/itemMaintenance", view);
        verify(searchService).mergeRequestWithCachedForm(form, request);
        verify(model).addAttribute("itemMaintenanceForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }
}

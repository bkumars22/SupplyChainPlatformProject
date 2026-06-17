/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;

import com.scplatform.pcm.forecast.dto.CostForecastVarianceForm;
import com.scplatform.pcm.searchframework.service.SearchService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CostForecastVarianceControllerTest {

    @Mock private SearchService searchService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;

    @InjectMocks
    private CostForecastVarianceController controller;

    private CostForecastVarianceForm form;

    @BeforeEach
    void setUp() {
        form = new CostForecastVarianceForm();
    }

    @Test
    void testInit_ReturnsViewName() throws Exception {
        String view = controller.init(form, request, response, model);
        assertEquals("costForecastVarianceSearchPage", view);
    }

    @Test
    void testInit_CallsSearchServiceWithExpectedProperties() throws Exception {
        controller.init(form, request, response, model);
        ArgumentCaptor<Properties> propsCap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(propsCap.capture(), eq(form), eq(request), eq(response));
        Properties p = propsCap.getValue();
        assertEquals("SearchDefCostForecastVariance.xml", p.get("definition"));
        assertEquals("costForecastVarianceSearch", p.get("searchAction"));
        assertEquals("false", p.get("enablePaging"));
    }

    @Test
    void testInit_PopulatesModel() throws Exception {
        controller.init(form, request, response, model);
        verify(model).addAttribute("costForecastVarianceForm", form);
        verify(model).addAttribute("definition", "SearchDefCostForecastVariance.xml");
        verify(model).addAttribute("searchAction", "costForecastVarianceSearch");
        verify(model).addAttribute("enablePaging", "false");
    }

    @Test
    void testInit_PropagatesException() throws Exception {
        doThrow(new RuntimeException("boom"))
                .when(searchService).init(any(Properties.class), eq(form), eq(request), eq(response));
        assertThrows(RuntimeException.class,
                () -> controller.init(form, request, response, model));
    }
}

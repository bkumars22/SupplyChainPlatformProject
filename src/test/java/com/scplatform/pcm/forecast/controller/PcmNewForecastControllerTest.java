/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
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
class PcmNewForecastControllerTest {

    @Mock private SearchService searchService;
    @Mock private PcmConfigUtil pcmConfigUtil;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;

    @InjectMocks
    private PcmNewForecastController controller;

    private ForecastForm form;

    @BeforeEach
    void setUp() {
        form = new ForecastForm("", new Object[]{});
    }

    @Test
    void testInit_ReturnsViewName() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        String view = controller.init(form, request, response, model);
        assertEquals("forecastItemSearchPage", view);
    }

    @Test
    void testInit_SetsShowAllColumnsFromConfig() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(false);
        controller.init(form, request, response, model);
        assertFalse(form.getShowAllColumns());
    }

    @Test
    void testInit_PopulatesPropertiesAndModel() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        controller.init(form, request, response, model);
        ArgumentCaptor<Properties> cap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(cap.capture(), eq(form), eq(request), eq(response));
        Properties p = cap.getValue();
        assertEquals("SearchDefForecastItem.xml", p.get("definition"));
        assertEquals("submitForecastItemSearch", p.get("searchAction"));
        assertEquals("setItemsNewForecast", p.get("nextAction"));
        assertEquals("COST", p.get("forecastType"));

        verify(model).addAttribute("forecastItemSearchForm", form);
        verify(model).addAttribute("definition", "SearchDefForecastItem.xml");
        verify(model).addAttribute("forecastType", "COST");
    }

    @Test
    void testSearch_HappyPath() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);

        String view = controller.search(form, request, response, model);
        assertEquals("forecastItemSearchPage", view);
        verify(searchService).search(any(Properties.class), eq(form), eq(request), eq(response));
        verify(model).addAttribute("forecastItemSearchForm", form);
    }

    @Test
    void testSearch_ExceptionPath_PopulatesSearchError() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);
        doThrow(new RuntimeException("crashed"))
                .when(searchService).search(any(Properties.class), eq(form), eq(request), eq(response));
        when(model.containsAttribute("forecastItemSearchForm")).thenReturn(false);

        String view = controller.search(form, request, response, model);
        assertEquals("forecastItemSearchPage", view);
        verify(model).addAttribute("searchError", "crashed");
    }
}

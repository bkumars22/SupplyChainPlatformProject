/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.controller;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.ForecastForm;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.service.PcmForecastService;
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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcmSearchForecastControllerTest {

    @Mock private SearchService searchService;
    @Mock private PcmConfigUtil pcmConfigUtil;
    @Mock private PcmForecastService pcmForecastService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Model model;

    @InjectMocks
    private PcmSearchForecastController controller;

    private ForecastForm form;

    @BeforeEach
    void setUp() {
        form = new ForecastForm("", new Object[]{});
    }

    @Test
    void testInit_ReturnsSearchPageView() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        String view = controller.init(form, request, response, model);
        assertEquals("forecastSearchPage", view);
        verify(searchService).init(any(Properties.class), eq(form), eq(request), eq(response));
        verify(model).addAttribute("forecastSearchForm", form);
        verify(model).addAttribute("forecastType", "COST");
    }

    @Test
    void testInit_AppliesFixedPresets() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        controller.init(form, request, response, model);
        assertEquals("COST", form.getPresetValues().get("forecastType"));
    }

    @Test
    void testSearch_HappyPath() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);
        String view = controller.search(form, request, response, model);
        assertEquals("forecastSearchPage", view);
        verify(searchService).search(any(Properties.class), eq(form), eq(request), eq(response));
    }

    @Test
    void testSearch_ExceptionPath_SetsSearchError() throws Exception {
        when(pcmConfigUtil.getBoolean(eq("pcm.forecastdetails.expanded"), eq(true))).thenReturn(true);
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);
        doThrow(new RuntimeException("oops"))
                .when(searchService).search(any(Properties.class), eq(form), eq(request), eq(response));
        when(model.containsAttribute("forecastSearchForm")).thenReturn(true);

        controller.search(form, request, response, model);
        verify(model).addAttribute("searchError", "oops");
    }

    @Test
    void testSetForecastKeys_HappyPathReturnsForecastPage() throws Exception {
        ForecastForm spiedForm = spy(form);
        // Populate selectedKeys via setSelectedPageKeys (which adds to the underlying Set)
        spiedForm.setSelectedPageKeys(new String[]{"1", "2", "abc"});
        when(searchService.mergeRequestWithCachedForm(eq(spiedForm), eq(request))).thenReturn(spiedForm);
        List<PcmForecast> forecasts = Arrays.asList(new PcmForecast(), new PcmForecast());
        // accept any list order — set iteration order is not guaranteed
        when(pcmForecastService.findAllByIds(anyList())).thenReturn(forecasts);

        String view = controller.setForecastKeys(spiedForm, request, response, model);
        assertEquals("forecastPage", view);
        verify(spiedForm).clearForecastRecords();
        verify(spiedForm).addForecastRecords(forecasts);
        verify(model).addAttribute("forecastForm", spiedForm);
        verify(model).addAttribute("forecastSearchForm", spiedForm);
    }

    @Test
    void testSetForecastKeys_NoSelectedKeys_DoesNotCallService() throws Exception {
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);
        // selectedKeys is an empty set by default — no numeric keys to look up
        String view = controller.setForecastKeys(form, request, response, model);
        assertEquals("forecastPage", view);
        verify(pcmForecastService, never()).findAllByIds(any());
    }

    @Test
    void testSetForecastKeys_EmptyAfterFiltering_DoesNotCallService() throws Exception {
        form.setSelectedPageKeys(new String[]{"", "non-numeric"});
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request))).thenReturn(form);
        String view = controller.setForecastKeys(form, request, response, model);
        assertEquals("forecastPage", view);
        verify(pcmForecastService, never()).findAllByIds(any());
    }

    @Test
    void testSetForecastKeys_ExceptionPath_ReturnsSearchPage() throws Exception {
        when(searchService.mergeRequestWithCachedForm(eq(form), eq(request)))
                .thenThrow(new RuntimeException("merge failed"));
        String view = controller.setForecastKeys(form, request, response, model);
        assertEquals("forecastSearchPage", view);
        verify(model).addAttribute("searchError", "merge failed");
    }
}

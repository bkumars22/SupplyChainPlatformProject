/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for the item-search endpoints that previously lived on
 * ItemSearchController and were merged into ItemController as part of
 * SCPlatform-10148 (search page migration). Class name preserved for stable
 * coverage history.
 */
class ItemSearchControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Model model;

    @InjectMocks
    private ItemController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitItemSearch() throws Exception {
        SearchForm form = new SearchForm();
        String view = controller.initItemSearch(form, request, response, model);

        assertEquals("/search/itemSearchPage", view);
        verify(model).addAttribute("itemSearchForm", form);

        ArgumentCaptor<Properties> propCap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(propCap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefItem.xml", propCap.getValue().get("definition"));
    }

    @Test
    void testSearchItem() throws Exception {
        SearchForm form = new SearchForm();
        SearchForm merged = new SearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.searchItem(form, request, response, model);

        assertEquals("/search/itemSearchPage", view);
        verify(model).addAttribute("itemSearchForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }

    @Test
    void testInitItemOnlySearch() throws Exception {
        SearchForm form = new SearchForm();
        String view = controller.init(form, request, response, model);

        assertEquals("/search/itemOnlySearchPage", view);
        verify(model).addAttribute("itemOnlySearchForm", form);

        ArgumentCaptor<Properties> propCap = ArgumentCaptor.forClass(Properties.class);
        verify(searchService).init(propCap.capture(), eq(form), eq(request), eq(response));
        assertEquals("SearchDefItemOnly.xml", propCap.getValue().get("definition"));
    }

    @Test
    void testSearchItemOnly() throws Exception {
        SearchForm form = new SearchForm();
        SearchForm merged = new SearchForm();
        when(searchService.mergeRequestWithCachedForm(form, request)).thenReturn(merged);

        String view = controller.search(form, request, response, model);

        assertEquals("/search/itemOnlySearchPage", view);
        verify(model).addAttribute("itemOnlySearchForm", merged);
        verify(searchService).search(any(Properties.class), eq(merged), eq(request), eq(response));
    }
}

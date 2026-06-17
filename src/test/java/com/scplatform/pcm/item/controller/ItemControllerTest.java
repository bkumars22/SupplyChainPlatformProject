/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

import com.scplatform.pcm.item.dto.ItemWrap;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.scplatform.pcm.ums.dto.ItemErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ItemController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetItemDetails_Success() throws Exception {
        Item item = mock(Item.class);
        ObjectNode node = new ObjectMapper().createObjectNode();
        node.put("itemKey", 1L);

        when(itemService.getItem(1L)).thenReturn(item);
        when(itemService.getInlineItemNaturalKeyAsJSON(item)).thenReturn(node);

        ResponseEntity<GenericResponse> resp = controller.getItemDetails(1L, request);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ItemWrap);
        Map<String, Object> itemMap = ((ItemWrap) resp.getBody()).getItem();
        assertNotNull(itemMap);
        assertEquals(1, ((Number) itemMap.get("itemKey")).intValue());
        verify(itemService).getItem(1L);
    }

    @Test
    void testGetItemDetails_NotFound() {
        when(itemService.getItem(99L)).thenReturn(null);

        ResponseEntity<GenericResponse> resp = controller.getItemDetails(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ItemErrorResponse);
    }

    @Test
    void testGetItemDetails_Exception() throws Exception {
        Item item = mock(Item.class);
        when(itemService.getItem(2L)).thenReturn(item);
        when(itemService.getInlineItemNaturalKeyAsJSON(item)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<GenericResponse> resp = controller.getItemDetails(2L, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertNotNull(resp.getBody());
        assertTrue(resp.getBody() instanceof ItemErrorResponse);
    }

    @Test
    void testGetItemDetails_ExceptionFromGetItem() {
        when(itemService.getItem(3L)).thenThrow(new RuntimeException("db down"));

        ResponseEntity<GenericResponse> resp = controller.getItemDetails(3L, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
        assertTrue(resp.getBody() instanceof ItemErrorResponse);
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.service;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.initializer.ItemEOLTypeListInitializer;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemEOLTypeListInitializerTest {

    private MockedStatic<SpringContextHolder> contextHolder;
    private PcmConfigUtil pcmConfig;
    private ItemEOLTypeListInitializer itemEOLTypeListInitializer;

    @BeforeEach
    void setUp() {
        pcmConfig = mock(PcmConfigUtil.class);
        contextHolder = mockStatic(SpringContextHolder.class);
        contextHolder.when(() -> SpringContextHolder.getBean(PcmConfigUtil.class)).thenReturn(pcmConfig);
        itemEOLTypeListInitializer = new ItemEOLTypeListInitializer(pcmConfig);
    }

    @AfterEach
    void tearDown() {
        contextHolder.close();
    }

    @Test
    void testInitializeParameter_AddsValuesIncludingActiveWithNullValue() {
        when(pcmConfig.getList(eq("pcm.itemEOL.type.keys"), anyList())).thenReturn(
                Arrays.asList("ACTIVE", "EOL", "OBSOLETE"));

        SearchParameterSelect select = mock(SearchParameterSelect.class);

        boolean result = itemEOLTypeListInitializer.initializeParameter(select, new HashMap<>());

        assertTrue(result);
        verify(select).addSelectValue("ACTIVE", null);
        verify(select).addSelectValue("EOL", "EOL");
        verify(select).addSelectValue("OBSOLETE", "OBSOLETE");
    }

    @Test
    void testInitializeParameter_NotSelect_ReturnsFalse() {
        SearchParameter param = mock(SearchParameter.class);
        assertFalse(itemEOLTypeListInitializer.initializeParameter(param, new HashMap<>()));
    }

    @Test
    void testSetInitialData_NoOp() {
        assertDoesNotThrow(() -> itemEOLTypeListInitializer.setInitialData("any"));
        assertDoesNotThrow(() -> itemEOLTypeListInitializer.setInitialData(null));
    }
}

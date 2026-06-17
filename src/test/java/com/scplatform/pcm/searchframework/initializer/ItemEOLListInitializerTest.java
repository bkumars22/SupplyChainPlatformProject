/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.searchframework.service.SearchParameterSelect;
import com.scplatform.pcm.searchframework.service.SearchParameterText;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemEOLListInitializerTest {

    @Mock
    private PcmConfigUtil pcmConfigUtil;

    @InjectMocks
    private ItemEOLListInitializer initializer;

    @Test
    void populatesSelectWithLookedUpDisplayValues() {
        when(pcmConfigUtil.getList(eq("pcm.item.eol.type.keys"), org.mockito.ArgumentMatchers.<List<String>>any()))
                .thenReturn(Arrays.asList("ACTIVE", "EOL"));
        when(pcmConfigUtil.getString(eq("pcm.item.eol.type.ACTIVE.value"), anyString())).thenReturn("Active");
        when(pcmConfigUtil.getString(eq("pcm.item.eol.type.EOL.value"), anyString())).thenReturn("End of Life");

        SearchParameterSelect param = new SearchParameterSelect("p", "lbl");
        assertTrue(initializer.initializeParameter(param, new HashMap<>()));
        assertEquals(2, param.getSelectValues().size());
        assertEquals("Active", param.getSelectValues().get("ACTIVE"));
        assertEquals("End of Life", param.getSelectValues().get("EOL"));
    }

    @Test
    void returnsFalseForNonSelectParameter() {
        SearchParameterText param = new SearchParameterText("p", "lbl");
        assertFalse(initializer.initializeParameter(param, Collections.emptyMap()));
    }

    @Test
    void setInitialDataIsNoop() {
        initializer.setInitialData("x");
    }
}

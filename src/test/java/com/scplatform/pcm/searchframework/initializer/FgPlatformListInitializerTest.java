/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.initializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
class FgPlatformListInitializerTest {

    @Mock
    private PcmConfigUtil pcmConfigUtil;

    @InjectMocks
    private FgPlatformListInitializer initializer;

    @Test
    void populatesSelectFromConfigList() {
        when(pcmConfigUtil.getList(eq("pcm.functional.platform.types"), org.mockito.ArgumentMatchers.<List<String>>any()))
                .thenReturn(Arrays.asList("X86", "ARM"));
        SearchParameterSelect param = new SearchParameterSelect("p", "lbl");
        Map<String, Object> ctx = new HashMap<>();

        boolean result = initializer.initializeParameter(param, ctx);

        assertTrue(result);
        assertEquals(2, param.getSelectValues().size());
        assertEquals("X86", param.getSelectValues().get("X86"));
        assertEquals("ARM", param.getSelectValues().get("ARM"));
    }

    @Test
    void returnsFalseForNonSelectParameter() {
        SearchParameterText param = new SearchParameterText("p", "lbl");
        assertFalse(initializer.initializeParameter(param, Collections.emptyMap()));
    }

    @Test
    void setInitialDataIsNoop() {
        initializer.setInitialData("anything");
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.DefaultExpressions;
import com.scplatform.pcm.searchframework.dto.SearchExpression;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.dto.SearchParameter.MatchType;

class SearchParameterTextTest {

    @Test
    void constructorDefaults() {
        SearchParameterText t = new SearchParameterText("n", "lbl");
        assertEquals("n", t.getName());
        assertEquals("lbl", t.getLabelKey());
        assertEquals(MatchType.EXACT, t.getMatchType());
        assertNull(t.getValue());
        assertNull(t.getFinderName());
        assertEquals("TEXT", t.getType());
        assertFalse(t.isValueArray());
        assertFalse(t.hasValue());
        assertFalse(t.getRequired());
    }

    @Test
    void labelKeyFallsBackToName() {
        SearchParameterText t = new SearchParameterText("n", null);
        assertEquals("n", t.getLabelKey());
    }

    @Test
    void fluentSetters() {
        SearchParameterText t = new SearchParameterText("n", "lbl");
        SearchParameter ret = t.setName("nn").setOptionId("oid").setLabelKey("lbl2")
                .setFinderName("f").setPopupFinderName("pf").setValue("v");
        assertSame(t, ret);
        assertEquals("nn", t.getName());
        assertEquals("oid", t.getOptionId());
        assertEquals("lbl2", t.getLabelKey());
        assertEquals("f", t.getFinderName());
        assertEquals("pf", t.getPopupFinderName());
        assertEquals("v", t.getValue());
        assertTrue(t.hasValue());
    }

    @Test
    void getValueForSQLReturnsRawValue() {
        SearchParameterText t = new SearchParameterText("n", "l");
        t.setValue("abc");
        assertEquals("abc", t.getValueForSQL());
    }

    @Test
    void hasValueFalseForNullEmptyStringAndArrays() {
        SearchParameterText t = new SearchParameterText("n", "l");
        assertFalse(t.hasValue());
        t.setValue("");
        assertFalse(t.hasValue());
        t.setValue(new Object[]{});
        assertFalse(t.hasValue());
        t.setValue(new Object[]{""});
        assertFalse(t.hasValue());
    }

    @Test
    void hasValueTrueForNonEmpty() {
        SearchParameterText t = new SearchParameterText("n", "l");
        t.setValue("x");
        assertTrue(t.hasValue());
        t.setValue(new Object[]{"a", "b"});
        assertTrue(t.hasValue());
        t.setValue(Integer.valueOf(5));
        assertTrue(t.hasValue());
    }

    @Test
    void searchExpressionAndDefaultExpressions() {
        SearchParameterText t = new SearchParameterText("n", "l");
        SearchExpression se = new SearchExpression("e");
        DefaultExpressions de = new DefaultExpressions();
        t.setSearchExpression(se);
        t.setDefaultExpressions(de);
        assertSame(se, t.getSearchExpression());
        assertSame(de, t.getDefaultExpressions());
    }

    @Test
    void matchTypeSetterAndGetter() {
        SearchParameterText t = new SearchParameterText("n", "l");
        t.setMatchType(MatchType.LIKE);
        assertEquals(MatchType.LIKE, t.getMatchType());
    }

    @Test
    void initializeWithoutInitializerReturnsFalse() {
        SearchParameterText t = new SearchParameterText("n", "l");
        assertFalse(t.initialize(new HashMap<>()));
    }

    @Test
    void initializeDelegatesToInitializer() {
        SearchParameterText t = new SearchParameterText("n", "l");
        SearchParameterInitializer init = mock(SearchParameterInitializer.class);
        Map<String, Object> ctx = Collections.emptyMap();
        when(init.initializeParameter(t, ctx)).thenReturn(true);
        t.setInitializer(init);
        assertTrue(t.initialize(ctx));
    }

    @Test
    void dataTypeFormatRequiredAndProperties() {
        SearchParameterText t = new SearchParameterText("n", "l");
        t.setDataType("Date");
        t.setDataFormat("yyyy");
        t.setRequired(true);
        t.setProperty("k", "v");
        assertEquals("Date", t.getDataType());
        assertEquals("yyyy", t.getDataFormat());
        assertTrue(t.getRequired());
        assertEquals("v", t.getProperties().get("k"));
        assertNotNull(t.getProperties());
    }
}

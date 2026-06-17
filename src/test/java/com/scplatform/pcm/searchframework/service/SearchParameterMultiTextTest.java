/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.SearchParameter.MatchType;

class SearchParameterMultiTextTest {

    @Test
    void typeIsMultiText() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ";");
        assertEquals("MULTITEXT", p.getType());
        assertFalse(p.isValueArray());
    }

    @Test
    void rowsSetterAndGetter() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        assertEquals(1, p.getRows());
        p.setRows(5);
        assertEquals(5, p.getRows());
    }

    @Test
    void delimiterSetterIgnoresNull() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        // Constructor has a known bug — assigns parameter to itself; delimiter remains default ","
        assertEquals(",", p.getDelimiter());
        p.setDelimiter(";");
        assertEquals(";", p.getDelimiter());
        p.setDelimiter(null);
        assertEquals(";", p.getDelimiter());
    }

    @Test
    void getValueForSqlSplitsByDelimiterAndTrims() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        p.setValue("  a , b ,c");
        Object result = p.getValueForSQL();
        assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) result);
    }

    @Test
    void getValueForSqlLowercasesWhenIExact() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        p.setMatchType(MatchType.IEXACT);
        p.setValue("ABC,DEF");
        Object result = p.getValueForSQL();
        assertArrayEquals(new String[]{"abc", "def"}, (String[]) result);
    }

    @Test
    void getValueForSqlAcceptsArrayInput() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        p.setValue(new Object[]{"a", "b"});
        Object result = p.getValueForSQL();
        assertArrayEquals(new String[]{"a", "b"}, (String[]) result);
    }

    @Test
    void trimArrayTrimsAllElements() {
        SearchParameterMultiText p = new SearchParameterMultiText("n", "l", ",");
        String[] in = {" a ", "b ", " c"};
        String[] out = p.trimArray(in);
        assertArrayEquals(new String[]{"a", "b", "c"}, out);
    }
}

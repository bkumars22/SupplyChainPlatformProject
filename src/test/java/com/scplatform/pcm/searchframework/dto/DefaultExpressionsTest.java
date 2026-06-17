/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

class DefaultExpressionsTest {

    @Test
    void emptyByDefault() {
        DefaultExpressions d = new DefaultExpressions();
        assertNotNull(d.getDefaultExprs());
        assertTrue(d.getDefaultExprs().isEmpty());
        assertTrue(d.getDefaultExpressionsNames().isEmpty());
    }

    @Test
    void setAndGetDefaultExpression() {
        DefaultExpressions d = new DefaultExpressions();
        d.setDefaultExpr("alpha", "EQ");
        d.setDefaultExpr("beta", "LIKE");

        assertEquals("EQ", d.getDefaultExpr("alpha"));
        assertEquals("LIKE", d.getDefaultExpr("beta"));
        assertNull(d.getDefaultExpr("missing"));
    }

    @Test
    void getDefaultExpressionsNamesPreservesOrder() {
        DefaultExpressions d = new DefaultExpressions();
        d.setDefaultExpr("first", "1");
        d.setDefaultExpr("second", "2");
        d.setDefaultExpr("third", "3");
        Set<String> names = d.getDefaultExpressionsNames();
        assertEquals(3, names.size());
        // LinkedHashMap preserves insertion order
        assertEquals("first", names.iterator().next());
    }

    @Test
    void overwriteSameKey() {
        DefaultExpressions d = new DefaultExpressions();
        d.setDefaultExpr("k", "v1");
        d.setDefaultExpr("k", "v2");
        assertEquals("v2", d.getDefaultExpr("k"));
        assertEquals(1, d.getDefaultExpressionsNames().size());
    }
}

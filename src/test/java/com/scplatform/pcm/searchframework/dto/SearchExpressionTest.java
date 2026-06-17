/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.SearchExpression.OperatorType;

class SearchExpressionTest {

    @Test
    void constructorAssignsExpressionAndNullOperator() {
        SearchExpression e = new SearchExpression("foo");
        assertEquals("foo", e.getExpression());
        assertNull(e.getOperator());
    }

    @Test
    void settersUpdateFields() {
        SearchExpression e = new SearchExpression("bar");
        e.setExpression("baz");
        e.setOperator(OperatorType.EQ);
        e.setDataType("Date");
        assertEquals("baz", e.getExpression());
        assertEquals(OperatorType.EQ, e.getOperator());
        assertEquals("Date", e.getDataType());
    }

    @Test
    void supportsWildcardTrueForLikeAndIlike() {
        SearchExpression e = new SearchExpression("x");
        e.setOperator(OperatorType.LIKE);
        assertTrue(e.getSupportsWildcard());
        e.setOperator(OperatorType.ILIKE);
        assertTrue(e.getSupportsWildcard());
    }

    @Test
    void supportsWildcardFalseForOtherOperators() {
        SearchExpression e = new SearchExpression("x");
        for (OperatorType op : OperatorType.values()) {
            if (op == OperatorType.LIKE || op == OperatorType.ILIKE) continue;
            e.setOperator(op);
            assertFalse(e.getSupportsWildcard(), "unexpected wildcard support for " + op);
        }
    }

    @Test
    void supportsWildcardFalseForNullOperator() {
        SearchExpression e = new SearchExpression("x");
        assertFalse(e.getSupportsWildcard());
    }

    @Test
    void operatorTypeHasExpectedValues() {
        assertEquals(11, OperatorType.values().length);
    }
}

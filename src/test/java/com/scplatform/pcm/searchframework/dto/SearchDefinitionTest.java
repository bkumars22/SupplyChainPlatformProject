/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.SearchDefinition.Order;

class SearchDefinitionTest {

    @Test
    void simpleSettersAndGetters() {
        SearchDefinition sd = new SearchDefinition();
        sd.setName("n");
        sd.setSource("src");
        sd.setGroupBy("g");
        sd.setSourceTransform("t");
        sd.setStartDisplayColumn(5L);
        sd.setExtractSource("es");
        sd.setExtractWriterClass("ewc");
        sd.setExtractWriterProp("ewp");
        sd.setExtractWriterTransform("ewt");
        sd.setExtractTemplateConfig("etc");
        sd.setExtractTemplate("et");
        sd.setExtractType("xls");
        sd.setCommodityProfileFilter("cpf");
        sd.setSourceQueryType("hql");
        sd.setExtractQueryType("sql");

        assertEquals("n", sd.getName());
        assertEquals("src", sd.getSource());
        assertEquals("g", sd.getGroupBy());
        assertEquals("t", sd.getSourceTransform());
        assertEquals(5L, sd.getStartDisplayColumn());
        assertEquals("es", sd.getExtractSource());
        assertEquals("ewc", sd.getExtractWriterClass());
        assertEquals("ewp", sd.getExtractWriterProp());
        assertEquals("ewt", sd.getExtractWriterTransform());
        assertEquals("etc", sd.getExtractTemplateConfig());
        assertEquals("et", sd.getExtractTemplate());
        assertEquals("xls", sd.getExtractType());
        assertEquals("cpf", sd.getCommodityProfileFilter());
        assertEquals("hql", sd.getSourceQueryType());
        assertEquals("sql", sd.getExtractQueryType());
    }

    @Test
    void addOrderByStoresFieldAndColumnMapping() {
        SearchDefinition sd = new SearchDefinition();
        sd.addOrderBy("price", "PRICE_COL");

        assertTrue(sd.getOrderByFields().contains("price"));
        assertEquals(Order.NOTSET, sd.getOrderBy("price"));
        assertEquals("price", sd.getOrderColumnMap().get("PRICE_COL"));
    }

    @Test
    void addOrderByWithNullColumnSkipsMapping() {
        SearchDefinition sd = new SearchDefinition();
        sd.addOrderBy("f", null);
        assertTrue(sd.getOrderColumnMap().isEmpty());
    }

    @Test
    void setOrderByStringResolvesAscDescAndOther() {
        SearchDefinition sd = new SearchDefinition();
        sd.setOrderBy("a", "asc");
        sd.setOrderBy("b", "DESC");
        sd.setOrderBy("c", "weird");
        assertEquals(Order.ASC, sd.getOrderBy("a"));
        assertEquals(Order.DESC, sd.getOrderBy("b"));
        assertEquals(Order.NOTSET, sd.getOrderBy("c"));
    }

    @Test
    void setOrderByEnumOverwrites() {
        SearchDefinition sd = new SearchDefinition();
        sd.setOrderBy("a", Order.ASC);
        sd.setOrderBy("a", Order.DESC);
        assertEquals(Order.DESC, sd.getOrderBy("a"));
    }

    @Test
    void addExpressionStoresAndReturnsExpression() {
        SearchDefinition sd = new SearchDefinition();
        SearchExpression se = sd.addExpression("name", "n.value");
        assertNotNull(se);
        assertEquals("n.value", se.getExpression());
        assertEquals(se, sd.getExpressions().get("name"));
    }

    @Test
    void setKeysParsesCommaSeparated() {
        SearchDefinition sd = new SearchDefinition();
        sd.setKeys("1,2,3");
        assertArrayEquals(new long[]{1L, 2L, 3L}, sd.getKeyColumns());
    }

    @Test
    void setKeysNullOrBlankLeavesKeyColumnsNull() {
        SearchDefinition sd = new SearchDefinition();
        sd.setKeys(null);
        assertNull(sd.getKeyColumns());
        sd.setKeys("   ");
        assertNull(sd.getKeyColumns());
    }

    @Test
    void setKeyColumnsStoresArray() {
        SearchDefinition sd = new SearchDefinition();
        long[] keys = new long[]{10L, 20L};
        sd.setKeyColumns(keys);
        assertArrayEquals(keys, sd.getKeyColumns());
    }

    @Test
    void getOrderByMapAndFields() {
        SearchDefinition sd = new SearchDefinition();
        sd.addOrderBy("a", null);
        sd.addOrderBy("b", null);
        assertEquals(2, sd.getOrderByMap().size());
        assertEquals(2, sd.getOrderByFields().size());
    }

    @Test
    void orderEnumValues() {
        assertEquals(3, Order.values().length);
    }
}

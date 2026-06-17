/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.dto;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BomCostRollupTest {

    @Test
    void defaultConstructor_initializesEmptyList() {
        BomCostRollup r = new BomCostRollup();
        assertNotNull(r.getJsonNodeList());
        assertTrue(r.getJsonNodeList().isEmpty());
    }

    @Test
    void newRow_returnsEmptyLinkedHashMap() {
        Map<String, Object> row = BomCostRollup.newRow();
        assertNotNull(row);
        assertTrue(row.isEmpty());
    }

    @Test
    void canAddRows() {
        BomCostRollup r = new BomCostRollup();
        Map<String, Object> row = BomCostRollup.newRow();
        row.put("ITEM_NAME", "X");
        r.getJsonNodeList().add(row);
        assertEquals(1, r.getJsonNodeList().size());
        assertEquals("X", r.getJsonNodeList().get(0).get("ITEM_NAME"));
    }

    @Test
    void setter_replacesList() {
        BomCostRollup r = new BomCostRollup();
        r.setJsonNodeList(java.util.Collections.emptyList());
        assertTrue(r.getJsonNodeList().isEmpty());
    }
}

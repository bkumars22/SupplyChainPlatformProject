/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.audit.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PcmAuditHistoryTest {

    @Test
    void noArgsConstructor_targetKeysDefaultsToEmptyList() {
        PcmAuditHistory h = new PcmAuditHistory();
        assertNull(h.getKey());
        assertNull(h.getActionDate());
        assertNull(h.getActionOrder());
        assertNull(h.getActionPerformed());
        assertNull(h.getUserId());
        assertNull(h.getRoleId());
        assertNull(h.getTargetType());
        assertNull(h.getSubTargetKey());
        assertNull(h.getSubTargetType());
        assertNull(h.getComment());
        assertNull(h.getLastLoadedByUser());
        assertNotNull(h.getTargetKeys());
        assertTrue(h.getTargetKeys().isEmpty());
    }

    @Test
    void allArgsConstructor_andGetters() {
        Timestamp ts = new Timestamp(100L);
        List<String> keys = Arrays.asList("k1", "k2");
        PcmAuditHistory h = new PcmAuditHistory("ID", ts, 5L, "CREATE", "u1", "r1", "Item",
                "sub-1", "Bom", "comment", "loader", keys);
        assertEquals("ID", h.getKey());
        assertEquals(ts, h.getActionDate());
        assertEquals(5L, h.getActionOrder());
        assertEquals("CREATE", h.getActionPerformed());
        assertEquals("u1", h.getUserId());
        assertEquals("r1", h.getRoleId());
        assertEquals("Item", h.getTargetType());
        assertEquals("sub-1", h.getSubTargetKey());
        assertEquals("Bom", h.getSubTargetType());
        assertEquals("comment", h.getComment());
        assertEquals("loader", h.getLastLoadedByUser());
        assertEquals(keys, h.getTargetKeys());
    }

    @Test
    void settersAndGetters() {
        PcmAuditHistory h = new PcmAuditHistory();
        h.setKey("K");
        h.setActionPerformed("UPDATE");
        h.setUserId("user");
        h.setRoleId("role");
        h.setTargetType("Item");
        h.setSubTargetKey("sk");
        h.setSubTargetType("st");
        h.setComment("c");
        h.setLastLoadedByUser("loader");
        h.setTargetKeys(Collections.singletonList("k"));
        h.setActionDate(new Timestamp(0L));
        h.setActionOrder(2L);

        assertEquals("K", h.getKey());
        assertEquals("UPDATE", h.getActionPerformed());
        assertEquals("user", h.getUserId());
        assertEquals("role", h.getRoleId());
        assertEquals("Item", h.getTargetType());
        assertEquals("sk", h.getSubTargetKey());
        assertEquals("st", h.getSubTargetType());
        assertEquals("c", h.getComment());
        assertEquals("loader", h.getLastLoadedByUser());
        assertEquals(Collections.singletonList("k"), h.getTargetKeys());
        assertEquals(new Timestamp(0L), h.getActionDate());
        assertEquals(2L, h.getActionOrder());
    }

    @Test
    void builder_setsFieldsAndDefaultsTargetKeys() {
        PcmAuditHistory h = PcmAuditHistory.builder()
                .key("K")
                .actionPerformed("DELETE")
                .userId("u")
                .build();
        assertEquals("K", h.getKey());
        assertEquals("DELETE", h.getActionPerformed());
        assertEquals("u", h.getUserId());
        assertNotNull(h.getTargetKeys());
        assertTrue(h.getTargetKeys().isEmpty());
    }

    @Test
    void equalsAndHashCode_lombokGenerated() {
        PcmAuditHistory a = PcmAuditHistory.builder().key("K").userId("u").build();
        PcmAuditHistory b = PcmAuditHistory.builder().key("K").userId("u").build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_containsKey() {
        PcmAuditHistory h = PcmAuditHistory.builder().key("ABC").build();
        assertTrue(h.toString().contains("ABC"));
    }
}

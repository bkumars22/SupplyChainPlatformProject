/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.dto;

import com.scplatform.pcm.cost.entity.PcmCostElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BomCostRollupFormTest {

    @Test
    void defaultConstructor_initializesEmptyCollections() {
        BomCostRollupForm form = new BomCostRollupForm();
        assertNotNull(form.getPcmCostElements());
        assertTrue(form.getPcmCostElements().isEmpty());
        assertNotNull(form.getCostElements());
        assertTrue(form.getCostElements().isEmpty());
    }

    @Test
    void setPcmCostElements_replacesCollection() {
        BomCostRollupForm form = new BomCostRollupForm();
        SortedSet<String> set = new TreeSet<>();
        set.add("DIRECT_LABOR");
        set.add("DIRECT_MATERIAL");
        form.setPcmCostElements(set);
        assertEquals(2, form.getPcmCostElements().size());
        assertEquals("DIRECT_LABOR", form.getPcmCostElements().first());
    }

    @Test
    void setCostElements_replacesList() {
        BomCostRollupForm form = new BomCostRollupForm();
        List<PcmCostElement> list = new ArrayList<>();
        list.add(new PcmCostElement());
        form.setCostElements(list);
        assertEquals(1, form.getCostElements().size());
    }

    @Test
    void extendsSearchForm() {
        assertTrue(com.scplatform.pcm.searchframework.dto.SearchForm.class
                .isAssignableFrom(BomCostRollupForm.class));
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.workflow.entity;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkflowTest {

    @Test
    void defaultConstructor_allFieldsNullOrZero() {
        Workflow w = new Workflow();
        assertNull(w.getWorkflowKey());
        assertNull(w.getWorkflowName());
        assertNull(w.getWorkflowGroup());
        assertNull(w.getWorkflowUrl());
        assertNull(w.getWorkflowUrlTarget());
        assertNull(w.getParentWorkflow());
        assertEquals(0, w.getDisplayOrder());
        assertNull(w.getNestedWorkflows());
        assertNull(w.getAcls());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Workflow parent = new Workflow();
        parent.setWorkflowKey("P");
        Set<Workflow> nested = new HashSet<>();
        Workflow w = new Workflow("K", "Name", "Group", "url", "target", parent, 5, nested, Collections.emptySet());
        assertEquals("K", w.getWorkflowKey());
        assertEquals("Name", w.getWorkflowName());
        assertEquals("Group", w.getWorkflowGroup());
        assertEquals("url", w.getWorkflowUrl());
        assertEquals("target", w.getWorkflowUrlTarget());
        assertEquals(parent, w.getParentWorkflow());
        assertEquals(5, w.getDisplayOrder());
        assertEquals(nested, w.getNestedWorkflows());
        assertNotNull(w.getAcls());
    }

    @Test
    void setters_work() {
        Workflow w = new Workflow();
        w.setWorkflowKey("K");
        w.setWorkflowName("N");
        w.setDisplayOrder(7);
        assertEquals("K", w.getWorkflowKey());
        assertEquals("N", w.getWorkflowName());
        assertEquals(7, w.getDisplayOrder());
    }

    @Test
    void equalsAndHashCode_basedOnSimpleFields() {
        Workflow w1 = new Workflow();
        w1.setWorkflowKey("K"); w1.setWorkflowName("N");
        Workflow w2 = new Workflow();
        w2.setWorkflowKey("K"); w2.setWorkflowName("N");
        assertEquals(w1, w2);
        assertEquals(w1.hashCode(), w2.hashCode());

        w2.setWorkflowName("N2");
        assertNotEquals(w1, w2);
    }

    @Test
    void toString_isNotNull() {
        Workflow w = new Workflow();
        w.setWorkflowKey("K");
        assertNotNull(w.toString());
    }
}

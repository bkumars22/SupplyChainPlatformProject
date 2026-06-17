/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.workflow.service;

import com.scplatform.pcm.workflow.entity.Workflow;
import com.scplatform.pcm.workflow.repository.WorkflowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkFlowServiceTest {

    @Mock
    private WorkflowRepository repo;

    @InjectMocks
    private WorkFlowService service;

    private Workflow parent;
    private Workflow child1;
    private Workflow child2;

    @BeforeEach
    void setUp() {
        parent = new Workflow();
        parent.setWorkflowKey("PARENT");
        parent.setWorkflowName("Parent");
        parent.setDisplayOrder(1);

        child1 = new Workflow();
        child1.setWorkflowKey("C1");
        child1.setWorkflowName("Child1");
        child1.setDisplayOrder(2);
        child1.setParentWorkflow(parent);

        child2 = new Workflow();
        child2.setWorkflowKey("C2");
        child2.setWorkflowName("Child2");
        child2.setDisplayOrder(1);
        child2.setParentWorkflow(parent);
    }

    @Test
    void getTopLevelWorkflows_returnsEmpty_whenNoneFound() {
        when(repo.findTopLevelWorkflows()).thenReturn(Collections.emptyList());
        List<Workflow> result = service.getTopLevelWorkflows();
        assertTrue(result.isEmpty());
    }

    @Test
    void getTopLevelWorkflows_returnsEmpty_whenRepoReturnsNull() {
        when(repo.findTopLevelWorkflows()).thenReturn(null);
        List<Workflow> result = service.getTopLevelWorkflows();
        assertTrue(result.isEmpty());
    }

    @Test
    void getTopLevelWorkflows_returnsEmpty_whenRepoThrows() {
        when(repo.findTopLevelWorkflows()).thenThrow(new RuntimeException("db"));
        List<Workflow> result = service.getTopLevelWorkflows();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getTopLevelWorkflows_initializesNullNestedSet() {
        parent.setNestedWorkflows(null);
        when(repo.findTopLevelWorkflows()).thenReturn(new ArrayList<>(Collections.singletonList(parent)));
        when(repo.findAllChildWorkflows()).thenReturn(Collections.emptyList());
        List<Workflow> result = service.getTopLevelWorkflows();
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getNestedWorkflows());
    }

    @Test
    void getTopLevelWorkflows_mergesChildrenIntoParent_andSorts() {
        parent.setNestedWorkflows(new HashSet<>());
        when(repo.findTopLevelWorkflows()).thenReturn(new ArrayList<>(Collections.singletonList(parent)));
        when(repo.findAllChildWorkflows()).thenReturn(Arrays.asList(child1, child2));

        List<Workflow> result = service.getTopLevelWorkflows();

        assertEquals(1, result.size());
        Workflow p = result.get(0);
        assertEquals(2, p.getNestedWorkflows().size());
        // sorted by displayOrder ascending: child2 (1), child1 (2)
        Workflow first = p.getNestedWorkflows().iterator().next();
        assertEquals("C2", first.getWorkflowKey());
    }

    @Test
    void getTopLevelWorkflows_skipsChildWithMissingParent() {
        Workflow orphanParent = new Workflow();
        orphanParent.setWorkflowKey("UNKNOWN");
        Workflow orphan = new Workflow();
        orphan.setWorkflowKey("OR");
        orphan.setParentWorkflow(orphanParent);

        parent.setNestedWorkflows(new HashSet<>());
        when(repo.findTopLevelWorkflows()).thenReturn(new ArrayList<>(Collections.singletonList(parent)));
        when(repo.findAllChildWorkflows()).thenReturn(Collections.singletonList(orphan));

        List<Workflow> result = service.getTopLevelWorkflows();
        assertEquals(0, result.get(0).getNestedWorkflows().size());
    }

    @Test
    void getTopLevelWorkflows_handlesNullChildList() {
        parent.setNestedWorkflows(new HashSet<>());
        when(repo.findTopLevelWorkflows()).thenReturn(new ArrayList<>(Collections.singletonList(parent)));
        when(repo.findAllChildWorkflows()).thenReturn(null);
        List<Workflow> result = service.getTopLevelWorkflows();
        assertEquals(1, result.size());
    }

    @Test
    void getBreadCrumb_returnsEmpty_whenUrlNotFound() {
        when(repo.findByWorkflowUrl("nope")).thenReturn(Optional.empty());
        Stack<Workflow> stk = service.getBreadCrumb("nope");
        assertNotNull(stk);
        assertTrue(stk.isEmpty());
    }

    @Test
    void getBreadCrumb_strippsQueryString_andRetries() {
        when(repo.findByWorkflowUrl("/page.do?x=1")).thenReturn(Optional.empty());
        when(repo.findByWorkflowUrl("/page.do")).thenReturn(Optional.of(parent));
        Stack<Workflow> stk = service.getBreadCrumb("/page.do?x=1");
        assertEquals(1, stk.size());
        assertEquals("PARENT", stk.peek().getWorkflowKey());
    }

    @Test
    void getBreadCrumb_walksParentChain() {
        Workflow gp = new Workflow();
        gp.setWorkflowKey("GP");
        parent.setParentWorkflow(gp);
        child1.setParentWorkflow(parent);
        when(repo.findByWorkflowUrl("/c1")).thenReturn(Optional.of(child1));
        Stack<Workflow> stk = service.getBreadCrumb("/c1");
        assertEquals(3, stk.size());
        assertEquals("GP", stk.peek().getWorkflowKey());
    }

    @Test
    void getBreadCrumb_returnsEmptyOnException() {
        when(repo.findByWorkflowUrl("/x")).thenThrow(new RuntimeException("boom"));
        Stack<Workflow> stk = service.getBreadCrumb("/x");
        assertNotNull(stk);
        assertTrue(stk.isEmpty());
    }

    @Test
    void getBreadCrumb_handlesNullUrl() {
        when(repo.findByWorkflowUrl(null)).thenReturn(Optional.empty());
        Stack<Workflow> stk = service.getBreadCrumb(null);
        assertNotNull(stk);
        assertTrue(stk.isEmpty());
        verify(repo).findByWorkflowUrl(null);
    }
}

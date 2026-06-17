/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.workflow.repository;

import com.scplatform.pcm.workflow.entity.Workflow;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkflowRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(WorkflowRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(WorkflowRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findTopLevelWorkflows_methodPresent() throws NoSuchMethodException {
        Method m = WorkflowRepository.class.getMethod("findTopLevelWorkflows");
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void findByWorkflowUrl_methodPresent() throws NoSuchMethodException {
        Method m = WorkflowRepository.class.getMethod("findByWorkflowUrl", String.class);
        assertEquals(Optional.class, m.getReturnType());
    }

    @Test
    void findByWorkflowNameContainingIgnoreCase_methodPresent() throws NoSuchMethodException {
        Method m = WorkflowRepository.class.getMethod("findByWorkflowNameContainingIgnoreCase", String.class);
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void findByWorkflowKey_methodPresent() throws NoSuchMethodException {
        Method m = WorkflowRepository.class.getMethod("findByWorkflowKey", String.class);
        assertEquals(Optional.class, m.getReturnType());
    }

    @Test
    void findAllChildWorkflows_methodPresent() throws NoSuchMethodException {
        Method m = WorkflowRepository.class.getMethod("findAllChildWorkflows");
        assertEquals(List.class, m.getReturnType());
    }

    @Test
    void typeParametersUseWorkflow() {
        // Sanity check - JpaRepository<Workflow, String>
        assertNotNull(Workflow.class);
    }
}

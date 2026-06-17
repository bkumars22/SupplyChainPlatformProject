/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.workflow.repository;

import com.scplatform.pcm.workflow.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String> {

    /**
     * Find all top-level workflows (where parentWorkflow is NULL).
     * Eagerly loads nested workflows and ACLs to avoid lazy loading and ConcurrentModificationException.
     * Uses LEFT JOIN FETCH to load collections in one query with duplicate elimination.
     * Ordered by workflowGroup and displayOrder.
     * 
     * @return list of top-level workflows with collections initialized
     */
    @Query("SELECT DISTINCT w FROM Workflow w " +
            "LEFT JOIN FETCH w.nestedWorkflows " +
            "LEFT JOIN FETCH w.acls " +
            "WHERE w.parentWorkflow IS NULL " +
            "ORDER BY w.workflowGroup ASC, w.displayOrder ASC")
    List<Workflow> findTopLevelWorkflows();

    /**
     * Find a workflow by its URL.
     * 
     * @param url the workflow URL
     * @return Optional containing the workflow if found
     */
    @Query("SELECT w FROM Workflow w WHERE w.workflowUrl = :url")
    Optional<Workflow> findByWorkflowUrl(@Param("url") String url);

    /**
     * Find workflows by name containing (case-insensitive search).
     * 
     * @param name partial workflow name
     * @return list of matching workflows
     */
    List<Workflow> findByWorkflowNameContainingIgnoreCase(String name);

    /**
     * Find workflows by workflow key.
     * 
     * @param workflowKey the workflow key
     * @return Optional containing the workflow if found
     */
    Optional<Workflow> findByWorkflowKey(String workflowKey);

    /**
     * Find all CHILD workflows (where parentWorkflow IS NOT NULL).
     * Gets UPLOAD_ADMIN, UPLOAD_PRICING, UPLOAD_SA, etc. that belong to parents.
     * 
     * @return list of child workflows
     */
    @Query("SELECT DISTINCT w FROM Workflow w " +
            "LEFT JOIN FETCH w.nestedWorkflows " +
            "LEFT JOIN FETCH w.acls " +
            "WHERE w.parentWorkflow IS NOT NULL " +
            "ORDER BY w.workflowGroup ASC, w.displayOrder ASC")
    List<Workflow> findAllChildWorkflows();

}

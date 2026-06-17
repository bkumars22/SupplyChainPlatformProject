/*
 * Created on Apr 18, 2005
 *
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.workflow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.workflow.entity.Workflow;
import com.scplatform.pcm.workflow.repository.WorkflowRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for Workflow and related operations.
 * Service layer handles business logic and orchestration.
 * 
 * @author bblasko
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkFlowService {
    private static final Logger logger = LoggerFactory.getLogger(WorkFlowService.class);
    
    private final WorkflowRepository workflowRepository;

    public List<Workflow> getTopLevelWorkflows() {
        try {
              List<Workflow> topLevelWorkflows = workflowRepository.findTopLevelWorkflows();
            logger.info("STEP 1: Fetched {} TOP-LEVEL workflows", topLevelWorkflows.size());
            
            // CHECK 1: Is topLevelWorkflows NULL or EMPTY?
            if (topLevelWorkflows == null || topLevelWorkflows.isEmpty()) {
                return new ArrayList<>();
            }
             List<Workflow> childWorkflows = workflowRepository.findAllChildWorkflows();
            
            // STEP 3: Create map of top-level workflows for O(1) lookup
            Map<String, Workflow> topLevelMap = new HashMap<>();
            for (Workflow workflow : topLevelWorkflows) {
                topLevelMap.put(workflow.getWorkflowKey(), workflow);
                // Initialize nestedWorkflows collection (CRITICAL for AppContextService)
                if (workflow.getNestedWorkflows() == null) {
                    workflow.setNestedWorkflows(new java.util.HashSet<>());
                    logger.debug("Initialized nestedWorkflows for: {}", workflow.getWorkflowKey());
                }
            }
            int mergedCount = 0;
            if (childWorkflows != null && !childWorkflows.isEmpty()) {
                for (Workflow child : childWorkflows) {
                    // CHECK 3: Does child have a parent?
                    if (child.getParentWorkflow() != null) {
                        String parentKey = child.getParentWorkflow().getWorkflowKey();
                        Workflow parent = topLevelMap.get(parentKey);
                        
                        // CHECK 4: Does parent exist in top-level map?
                        if (parent != null) {
                            parent.getNestedWorkflows().add(child);
                            mergedCount++;
                            logger.debug(" Merged child {} into parent {}",
                                    child.getWorkflowKey(), parentKey);
                        } else {
                            logger.warn("Child {} references non-existent parent {}",
                                    child.getWorkflowKey(), parentKey);
                        }
                    }
                }
            }
             int sortedParentCount = 0;
            for (Workflow parent : topLevelWorkflows) {
                if (parent.getNestedWorkflows() != null && !parent.getNestedWorkflows().isEmpty()) {
                    List<Workflow> sortedChildren = new ArrayList<>(parent.getNestedWorkflows());
                    sortedChildren.sort((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()));
                    parent.setNestedWorkflows(new java.util.LinkedHashSet<>(sortedChildren));
                    sortedParentCount++;
                    for (Workflow child : sortedChildren) {
                        logger.info(" {}: {} (order={})",
                                child.getWorkflowKey(), 
                                child.getWorkflowName(), 
                                child.getDisplayOrder());
                    }
                }
            }
            if (topLevelWorkflows.isEmpty()) {
                logger.warn("FINAL: topLevelWorkflows is EMPTY after processing - Menu will NOT show");
                return new ArrayList<>();
            }
            return topLevelWorkflows;
            
        } catch (Exception e) {
            logger.error(" ERROR in getTopLevelWorkflows: {}", e.getMessage(), e);
            return new ArrayList<>();  // Return empty if error - menu will not show
        }
    }
    
    /**
     * Get the breadcrumb path for a workflow by URL.
     * Builds a stack from the workflow up to its root parent.
     * 
     * @param url the workflow URL
     * @return Stack containing the workflow and its parents (top = root parent)
     */
    public Stack<Workflow> getBreadCrumb(String url) {
        Stack<Workflow> breadCrumbStack = new Stack<>();
        
        try {
            // Try exact match first; fall back to URL without query string
            Optional<Workflow> match = workflowRepository.findByWorkflowUrl(url);
            if (match.isEmpty()) {
                int qi = url == null ? -1 : url.indexOf('?');
                if (qi > 0) {
                    match = workflowRepository.findByWorkflowUrl(url.substring(0, qi));
                }
            }

            // Fallback for detail pages not in PCM_WORKFLOW_SPRING is not needed:
            // goViewJob (uploadFile.jsp) uses form submit which does not change iframe.src,
            // so contentFrameLoaded reads the old uploadFile.do URL which is already in DB.

            match.ifPresent(workflow -> {
                breadCrumbStack.push(workflow);
                Workflow current = workflow;
                
                while (current.getParentWorkflow() != null) {
                    breadCrumbStack.push(current.getParentWorkflow());
                    current = current.getParentWorkflow();
                }
            });
        } catch (Exception e) {
            logger.error("Error fetching breadcrumb for URL {}: {}", url, e.getMessage(), e);
        }
        
        return breadCrumbStack;
    }

}

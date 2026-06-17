/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import com.scplatform.pcm.alert.exception.AlertQueueException;
import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Business-facing API for creating and committing alert events.
 *
 * <p>This is the entry point that business logic (CostRecordAction, ForecastLoader, etc.)
 * calls to generate alerts. It replaces the old pattern of:</p>
 * <pre>
 *   AlertEvent aeo = new AlertEvent();
 *   aeo.setAlertType(AlertTypes.CostPending);
 *   aeo.setObjectKey(costRecordKey);
 *   // ... set fields ...
 *   aeo.commit();  // → AlertHandler.getInstance().queue(this)
 * </pre>
 *
 * <p>New pattern:</p>
 * <pre>
 *   alertFacade.recordCostPendingAlert(costRecordKey, itemKey, actorUserId, metadata, changes);
 *   // or more generically:
 *   alertFacade.createAndCommit(AlertTypes.CostPending, costRecordKey, ObjectTypes.COST_RECORD,
 *                               itemKey, actorUserId, metadata, changes, receivers);
 * </pre>
 *
 * <h3>Design Decision:</h3>
 * <p>Alert failures are <strong>non-fatal</strong>. If an alert cannot be queued,
 * the business operation continues. The error is logged but NOT propagated.</p>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertFacade {

    private static final Logger log = LoggerFactory.getLogger(AlertFacade.class);

    private final AlertHandler alertHandler;

    public AlertFacade(AlertHandler alertHandler) {
        this.alertHandler = alertHandler;
    }

    /**
     * Creates and commits an alert event to the Artemis queue.
     *
     * @param alertType    type of alert
     * @param objectKey    primary key of the business object
     * @param objectType   type of the business object
     * @param referenceKey reference key (usually Item key)
     * @param actor        user who triggered the alert
     * @param metadata     additional context data
     * @param changes      list of changed field names
     * @param receivers    explicit receivers (null to use subscription lookup)
     */
    public void createAndCommit(AlertTypes alertType, Long objectKey, ObjectTypes objectType,
                                Long referenceKey, Long actor,
                                Map<String, Object> metadata, List<String> changes,
                                List<AlertReceiver> receivers) {
        createAndCommit(alertType, objectKey, objectType, referenceKey, actor,
                metadata, null, changes, receivers);
    }

    /**
     * Creates and commits an alert event to the Artemis queue (with filters).
     *
     * <p>Filters are used by AlertSubscriptionService to match subscriber preferences
     * (e.g., filter by ItemKey, CostType, State, Supplier, Site).</p>
     *
     * @param alertType    type of alert
     * @param objectKey    primary key of the business object
     * @param objectType   type of the business object
     * @param referenceKey reference key (usually Item key)
     * @param actor        user who triggered the alert
     * @param metadata     additional context data (for template evaluation + flex attributes)
     * @param filters      subscription filter criteria (ItemKey, CostType, State, etc.)
     * @param changes      list of changed field names
     * @param receivers    explicit receivers (null to use subscription lookup)
     */
    public void createAndCommit(AlertTypes alertType, Long objectKey, ObjectTypes objectType,
                                Long referenceKey, Long actor,
                                Map<String, Object> metadata, Map<String, Object> filters,
                                List<String> changes, List<AlertReceiver> receivers) {
        try {
            AlertEvent event = AlertEvent.builder()
                    .alertType(alertType)
                    .objectKey(objectKey)
                    .objectType(objectType)
                    .referenceKey(referenceKey)
                    .referenceType(ObjectTypes.ITEM)
                    .actor(actor)
                    .build();

            if (metadata != null) {
                event.setMetadata(metadata);
            }
            if (changes != null) {
                event.setChanges(changes);
            }
            if (receivers != null) {
                event.setReceivers(receivers);
            }
            if (filters != null) {
                event.setFilters(filters);
            }

            // Mark as committed and queue
            event.commit();
            alertHandler.queue(event);

        } catch (AlertQueueException e) {
            // Non-fatal: log error but don't fail the business operation
            log.error("Could not commit alert event of type [{}] for object [{}]. " +
                    "Business operation will continue.", alertType, objectKey, e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Convenience methods for specific alert types
    // ─────────────────────────────────────────────────────────────

    /** Record a cost change alert */
    public void recordCostChangeAlert(Long costRecordKey, Long itemKey, Long actor,
                                      Map<String, Object> metadata, List<String> changes) {
        createAndCommit(AlertTypes.CostChange, costRecordKey, ObjectTypes.COST_RECORD,
                itemKey, actor, metadata, changes, null);
    }

    /** Record a cost pending (awaiting approval) alert */
    public void recordCostPendingAlert(Long costRecordKey, Long itemKey, Long actor,
                                       Map<String, Object> metadata, List<String> changes) {
        createAndCommit(AlertTypes.CostPending, costRecordKey, ObjectTypes.COST_RECORD,
                itemKey, actor, metadata, changes, null);
    }

    /** Record a forecast change alert */
    public void recordForecastChangeAlert(Long forecastKey, Long itemKey, Long actor,
                                          Map<String, Object> metadata, List<String> changes) {
        createAndCommit(AlertTypes.ForecastChange, forecastKey, ObjectTypes.FORECAST,
                itemKey, actor, metadata, changes, null);
    }

    /** Record a forecast pending (awaiting approval) alert */
    public void recordForecastPendingAlert(Long forecastKey, Long itemKey, Long actor,
                                           Map<String, Object> metadata) {
        createAndCommit(AlertTypes.ForecastPending, forecastKey, ObjectTypes.FORECAST,
                itemKey, actor, metadata, null, null);
    }

    /** Record a supply allocation change alert */
    public void recordSupplyAllocationChangeAlert(Long allocationKey, Long itemKey, Long actor,
                                                  Map<String, Object> metadata, List<String> changes) {
        createAndCommit(AlertTypes.SupplyAllocationChange, allocationKey, ObjectTypes.SUPPLY_ALLOCATION,
                itemKey, actor, metadata, changes, null);
    }

    /** Record a supply allocation missing alert */
    public void recordSupplyAllocationMissingAlert(Long itemKey, Long actor,
                                                   Map<String, Object> metadata) {
        createAndCommit(AlertTypes.SupplyAllocationMissing, null, ObjectTypes.ITEM,
                itemKey, actor, metadata, null, null);
    }

    /**
     * Record an item assignment alert.
     * Legacy equivalent: AlertFacade.recordItemAssignmentAlert(alertDef, actor, item, assignee, metaContext)
     *
     * @param itemKey    the item being assigned
     * @param actor      user who performed the assignment
     * @param metadata   must contain "assignee" and "responsibility" keys (from legacy metaContext)
     * @param receivers  explicit receivers (the assignee user)
     */
    public void recordItemAssignmentAlert(Long itemKey, Long actor,
                                          Map<String, Object> metadata,
                                          List<AlertReceiver> receivers) {
        createAndCommit(AlertTypes.ItemAssignment, itemKey, ObjectTypes.ITEM,
                itemKey, actor, metadata, null, receivers);
    }

    /**
     * Record an item unassignment alert.
     * Legacy equivalent: AlertFacade.recordItemUnassignmentAlert(alertDef, actor, item, resp, unassignee, metaContext)
     *
     * @param itemKey    the item being unassigned
     * @param actor      user who performed the unassignment
     * @param metadata   must contain "Responsibility" and "unassignee" keys (from legacy metaContext)
     * @param receivers  explicit receivers (the unassignee user)
     */
    public void recordItemUnassignmentAlert(Long itemKey, Long actor,
                                            Map<String, Object> metadata,
                                            List<AlertReceiver> receivers) {
        createAndCommit(AlertTypes.ItemUnassignment, itemKey, ObjectTypes.ITEM,
                itemKey, actor, metadata, null, receivers);
    }

    /** Record a cost expiring alert */
    public void recordCostExpiringAlert(Long costRecordKey, Long itemKey, Long actor,
                                        Map<String, Object> metadata) {
        createAndCommit(AlertTypes.CostExpiring, costRecordKey, ObjectTypes.COST_RECORD,
                itemKey, actor, metadata, null, null);
    }

    /** Record a cost missing alert */
    public void recordCostMissingAlert(Long itemKey, Long actor, Map<String, Object> metadata) {
        createAndCommit(AlertTypes.CostMissing, null, ObjectTypes.ITEM,
                itemKey, actor, metadata, null, null);
    }

    /** Record a BOM attrition rate missing alert */
    public void recordBOMAttritionRateMissingAlert(Long bomKey, Long itemKey, Long actor,
                                                   Map<String, Object> metadata) {
        createAndCommit(AlertTypes.BOMAttritionRateMissing, bomKey, ObjectTypes.BOM,
                itemKey, actor, metadata, null, null);
    }
}


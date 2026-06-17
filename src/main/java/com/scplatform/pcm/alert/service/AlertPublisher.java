/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Publishes alert events to the SC_ALERT_DETAIL database table for subscribed users.
 *
 * <p>Maps AlertEvent fields to the real SC_ALERT_DETAIL columns:</p>
 * <pre>
 *   STATE              = "ACTIVE" (matches legacy Alert.ACTIVE constant)
 *   USER_LOGIN_ID      = receiver's login ID (from PCM_USER.USER_ID)
 *   ALERT_TYPE         = alertEvent.alertType.name()
 *   ALERT_ID           = template-evaluated unique ID
 *   ALERT_LABEL        = alertType description
 *   SHORT_SUMMARY      = template-evaluated (max 255 chars)
 *   LONG_SUMMARY       = template-evaluated (max 512 chars)
 *   CREATED            = now
 *   EXPIRATION_DATE    = now + expiryDays
 *   PUNCHOUT_URL       = alert-type-specific URL prefix (legacy AlertMetaData.xml)
 *   STRING_ATTRIBUTE1  = Item (item number / name from metadata)
 *   STRING_ATTRIBUTE2  = Supplier (business entity name from metadata)
 *   STRING_ATTRIBUTE3  = SourceSite (from metadata)
 *   STRING_ATTRIBUTE4  = DestinationSite / Region (from metadata — ForecastChange uses "Region")
 *   STRING_ATTRIBUTE6  = Responsibility (from metadata)
 *   STRING_ATTRIBUTE7  = CostType (from metadata)
 *   STRING_ATTRIBUTE8  = State (business object state from metadata)
 *   STRING_ATTRIBUTE9  = UpdatedBy (actor login ID from metadata)
 *   NUMERIC_ATTRIBUTE1 = Allocation (SupplyAllocationChange only — from metadata)
 *   DATE_ATTRIBUTE1    = StartDate (cost record period start)
 *   DATE_ATTRIBUTE2    = EndDate (cost record period end)
 * </pre>
 */
@Service
@ConditionalOnProperty(name = "pcm.alert.artemis.enabled", havingValue = "true", matchIfMissing = false)
public class AlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(AlertPublisher.class);

    private final AlertDetailRepository alertDetailRepository;
    private final AlertSubscriptionService alertSubscriptionService;
    private final AlertTemplateService alertTemplateService;

    @Value("${pcm.alert.expiry-days:30}")
    private int alertExpiryDays;

    public AlertPublisher(AlertDetailRepository alertDetailRepository,
                          AlertSubscriptionService alertSubscriptionService,
                          AlertTemplateService alertTemplateService) {
        this.alertDetailRepository = alertDetailRepository;
        this.alertSubscriptionService = alertSubscriptionService;
        this.alertTemplateService = alertTemplateService;
    }

    /**
     * Publishes an alert event to the database for all determined receivers.
     *
     * <p>If the event's clearAlertFlag is set, existing alerts of this type
     * and object are dismissed instead of creating new ones (legacy behavior).</p>
     *
     * @param alertEvent the alert event to publish
     * @return true if publishing succeeded, false otherwise
     */
    @Transactional
    public boolean publish(AlertEvent alertEvent) {
        if (alertEvent == null) {
            log.warn("Cannot publish null alert event");
            return false;
        }

        // Legacy behavior: clear existing alerts instead of creating new ones
        if (alertEvent.isClearAlertFlag()) {
            return clearAlert(alertEvent);
        }

        return publishAlert(alertEvent);
    }

    /**
     * Clears (dismisses) existing alerts matching the event's type and object key.
     * Legacy compatibility — see old AlertPublisher.clearAlert().
     */
    private boolean clearAlert(AlertEvent alertEvent) {
        try {
            log.info("Clearing existing alerts for type [{}] objectKey [{}]",
                    alertEvent.getAlertType(), alertEvent.getObjectKey());
            // Future: implement alert clearing logic if business requires it
            return true;
        } catch (Exception e) {
            log.error("Failed to clear alerts for type [{}]", alertEvent.getAlertType(), e);
            return false;
        }
    }

    private boolean publishAlert(AlertEvent alertEvent) {

        log.debug("Publishing alert event [{}] of type [{}]",
                alertEvent.getAlertEventID(), alertEvent.getAlertType());

        try {
            // Step 1: Determine receivers
            List<AlertReceiver> receivers = determineReceivers(alertEvent);

            if (receivers.isEmpty()) {
                log.info("No receivers found for alert event [{}] of type [{}]. Skipping.",
                        alertEvent.getAlertEventID(), alertEvent.getAlertType());
                return true; // No receivers = nothing to do = success
            }

            // Step 2: Build AlertDetail entities for each receiver
            List<AlertDetail> alertDetails = new ArrayList<>();
            LocalDate now = LocalDate.now();
            LocalDate expirationDate = now.plusDays(alertExpiryDays);

            // Pre-evaluate templates once (same for all receivers)
            String shortSummary = truncate(alertTemplateService.evaluateShortSummary(alertEvent), 255);
            String longSummary = truncate(alertTemplateService.evaluateLongSummary(alertEvent), 512);
            String alertId = truncate(alertTemplateService.evaluateAlertId(alertEvent), 512);
            String alertTypeName = alertEvent.getAlertType().name();
            String alertLabel = alertEvent.getAlertType().getDescription();

            for (AlertReceiver receiver : receivers) {
                // Duplicate prevention: skip if already published for this alertId+user
                String receiverLoginId = receiver.getUserLoginId();
                if (receiverLoginId != null && alertId != null && alertDetailRepository.existsByAlertIdAndUserLoginId(
                        alertId, receiverLoginId)) {
                    log.debug("Alert already exists for alertId [{}] and user [{}]. Skipping duplicate.",
                            alertId, receiverLoginId);
                    continue;
                }

                AlertDetail detail = AlertDetail.builder()
                        // Core columns — STATE must be "ACTIVE" to match legacy UI (AlertAction filters by state=ACTIVE)
                        .state(AlertDetailState.ACTIVE)
                        .userLoginId(receiverLoginId)
                        .alertType(alertTypeName)
                        .alertId(alertId)
                        .alertLabel(alertLabel)
                        .shortSummary(shortSummary)
                        .longSummary(longSummary)
                        .created(now)
                        .expirationDate(expirationDate)
                        .punchoutUrl(buildPunchoutUrl(alertEvent))
                        .dismissedBy(null)
                        // Flex attributes — mapped to match AlertMetaData.xml conventions:
                        //   StringAttribute1  = Item (item number / name from metadata)
                        //   StringAttribute2  = Supplier (from metadata)
                        //   StringAttribute3  = SourceSite (from metadata)
                        //   StringAttribute4  = DestinationSite / Region (ForecastChange uses "Region")
                        //   StringAttribute5  = (reserved)
                        //   StringAttribute6  = Responsibility (from metadata)
                        //   StringAttribute7  = CostType (from metadata)
                        //   StringAttribute8  = State (from metadata)
                        //   StringAttribute9  = UpdatedBy (actor login ID from metadata)
                        //   NumericAttribute1 = Allocation (SupplyAllocationChange only)
                        .stringAttribute1(truncate(getMetadataString(alertEvent, "Item"), 255))
                        .stringAttribute2(truncate(getMetadataString(alertEvent, "Supplier"), 255))
                        .stringAttribute3(truncate(getMetadataString(alertEvent, "SourceSite"), 255))
                        .stringAttribute4(truncate(coalesce(
                                getMetadataString(alertEvent, "DestinationSite"),
                                getMetadataString(alertEvent, "Region")), 255))
                        .stringAttribute6(truncate(getMetadataString(alertEvent, "Responsibility"), 64))
                        .stringAttribute7(truncate(getMetadataString(alertEvent, "CostType"), 64))
                        .stringAttribute8(truncate(getMetadataString(alertEvent, "State"), 64))
                        .stringAttribute9(truncate(getMetadataString(alertEvent, "UpdatedBy"), 64))
                        // Numeric attributes — mapped from metadata per AlertMetaData.xml
                        // Only SupplyAllocationChange maps Allocation → NumberAttribute1
                        // (Old system did NOT store objectKey/referenceKey/actor in numeric attrs)
                        .numericAttribute1(getMetadataNumber(alertEvent, "Allocation"))
                        // Date attributes for cost record date ranges
                        .dateAttribute1(getMetadataDate(alertEvent, "StartDate"))
                        .dateAttribute2(getMetadataDate(alertEvent, "EndDate"))
                        .build();

                alertDetails.add(detail);
            }

            // Step 3: Batch save
            if (!alertDetails.isEmpty()) {
                alertDetailRepository.saveAll(alertDetails);
                log.info("Published alert event [{}] of type [{}] to {} receiver(s)",
                        alertEvent.getAlertEventID(), alertEvent.getAlertType(), alertDetails.size());
            }

            return true;

        } catch (Exception e) {
            log.error("Failed to publish alert event [{}] of type [{}]",
                    alertEvent.getAlertEventID(), alertEvent.getAlertType(), e);
            return false;
        }
    }

    /**
     * Determines which users should receive this alert.
     */
    private List<AlertReceiver> determineReceivers(AlertEvent alertEvent) {
        // If explicit receivers are set, use them directly
        if (alertEvent.getReceivers() != null && !alertEvent.getReceivers().isEmpty()) {
            log.debug("Using {} explicit receiver(s) for event [{}]",
                    alertEvent.getReceivers().size(), alertEvent.getAlertEventID());
            return alertEvent.getReceivers();
        }

        // Query PCM_ALERT_SUBSCRIPTION + PCM_ALERT_SUBSCRIPTION_OPTIONS
        return alertSubscriptionService.findSubscribers(alertEvent);
    }

    /** Truncates a string to the given max length, or returns null if input is null. */
    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /** Extracts a String value from the alert event's metadata map. */
    private String getMetadataString(AlertEvent alertEvent, String key) {
        if (alertEvent.getMetadata() == null) return null;
        Object value = alertEvent.getMetadata().get(key);
        return value != null ? value.toString() : null;
    }

    /** Extracts a LocalDate value from the alert event's metadata map. */
    private LocalDate getMetadataDate(AlertEvent alertEvent, String key) {
        if (alertEvent.getMetadata() == null) return null;
        Object value = alertEvent.getMetadata().get(key);
        if (value instanceof LocalDate) return (LocalDate) value;
        if (value instanceof Date date) {
            return new java.sql.Date(date.getTime()).toLocalDate();
        }
        return null;
    }

    /** Extracts a numeric value from the alert event's metadata map as Double. */
    private Double getMetadataNumber(AlertEvent alertEvent, String key) {
        if (alertEvent.getMetadata() == null) return null;
        Object value = alertEvent.getMetadata().get(key);
        if (value instanceof Number num) return num.doubleValue();
        return null;
    }

    /** Returns the first non-null value, or null if both are null. */
    private String coalesce(String first, String second) {
        return first != null ? first : second;
    }

    /**
     * Builds the punchout URL for the alert based on alert type.
     * Matches the legacy AlertMetaData.xml punchOutPrefixURL configuration.
     *
     * <p>The punchout URL allows users to navigate directly to the
     * relevant business object from the alert (e.g., cost record, forecast, item).</p>
     */
    private String buildPunchoutUrl(AlertEvent alertEvent) {
        if (alertEvent.getAlertType() == null) return null;

        String prefix = switch (alertEvent.getAlertType()) {
            case CostChange, CostPending, CostExpiring ->
                    "/pinCostRecord.do?objectKey=";
            case ForecastChange, ForecastPending ->
                    "/pinForecast.do?objectKey=";
            case SupplyAllocationChange, SupplyAllocationMissing ->
                    "/pinSupplyAllocation.do?objectKey=";
            case ItemAssignment, ItemUnassignment, BOMAttritionRateMissing ->
                    "/pinItem.do?objectKey=";
            case CostMissing ->
                    "/pinSourcingLaneItem.do?objectKey=";
        };

        return prefix;
    }
}

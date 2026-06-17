/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import com.scplatform.pcm.alert.enums.AlertDetailState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * JPA entity mapped to the existing SC_ALERT_DETAIL table.
 *
 * <p>Each row represents a published alert for a specific user.
 * One AlertEvent can generate multiple AlertDetail rows — one per receiver.</p>
 *
 * <p>DB Schema (existing — do NOT modify the table):</p>
 * <pre>
 *   ID                 NUMBER(19)    PK (sequence: SC_ALERT_SEQ)
 *   STATE              VARCHAR2(64)
 *   DISMISED_BY        VARCHAR2(64)  — note: typo in original DB
 *   USER_LOGIN_ID      VARCHAR2(64)
 *   ALERT_LABEL        VARCHAR2(255)
 *   ALERT_ID           VARCHAR2(512)
 *   ALERT_TYPE         VARCHAR2(255)
 *   CREATED            DATE
 *   SHORT_SUMMARY      VARCHAR2(255)
 *   LONG_SUMMARY       VARCHAR2(512)
 *   EXPIRATION_DATE    DATE
 *   PUNCHOUT_URL       VARCHAR2(1024)
 *   STRING_ATTRIBUTE1..20  VARCHAR2
 *   DATE_ATTRIBUTE1..10    DATE
 *   NUMERIC_ATTRIBUTE1..10 FLOAT(126)
 * </pre>
 *
 * <h3>Extension Attribute Conventions (matching legacy AlertMetaData.xml):</h3>
 * <pre>
 *   STRING_ATTRIBUTE1  = Item (item number/name)
 *   STRING_ATTRIBUTE2  = Supplier (business entity name)
 *   STRING_ATTRIBUTE3  = SourceSite
 *   STRING_ATTRIBUTE4  = DestinationSite / Region (ForecastChange uses "Region")
 *   STRING_ATTRIBUTE5  = (reserved)
 *   STRING_ATTRIBUTE6  = Responsibility (comma-delimited list)
 *   STRING_ATTRIBUTE7  = CostType
 *   STRING_ATTRIBUTE8  = State (business object state)
 *   STRING_ATTRIBUTE9  = UpdatedBy (actor login ID)
 *   NUMERIC_ATTRIBUTE1 = Allocation (SupplyAllocationChange only — from metadata)
 *   DATE_ATTRIBUTE1    = StartDate (cost record period start)
 *   DATE_ATTRIBUTE2    = EndDate (cost record period end)
 * </pre>
 */
@Entity
@Table(name = "SC_ALERT_DETAIL")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc_alert_seq")
    @SequenceGenerator(
            name = "sc_alert_seq",
            sequenceName = "SC_ALERT_SEQ",
            allocationSize = 1
    )
    @Column(name = "ID")
    private Long id;

    /** Alert lifecycle state: ACTIVE (visible to user) or DISMISSED */
    @Column(name = "STATE", length = 64)
    @Enumerated(EnumType.STRING)
    private AlertDetailState state;

    /** Login ID of the user who dismissed this alert (DB column has typo: DISMISED_BY) */
    @Column(name = "DISMISED_BY", length = 64)
    private String dismissedBy;

    /** Login ID of the user who receives this alert (maps to PCM_USER.USER_ID) */
    @Column(name = "USER_LOGIN_ID", length = 64)
    private String userLoginId;

    /** Display label for the alert */
    @Column(name = "ALERT_LABEL", length = 255)
    private String alertLabel;

    /** Unique alert identifier (e.g., "CostChange-12345-abc123") */
    @Column(name = "ALERT_ID", length = 512)
    private String alertId;

    /** Alert type name (e.g., "CostChange") */
    @Column(name = "ALERT_TYPE", length = 255)
    private String alertType;

    /** When the alert was created */
    @Column(name = "CREATED")
    private LocalDate created;

    /** Short summary for alert list view (max 255 chars) */
    @Column(name = "SHORT_SUMMARY", length = 255)
    private String shortSummary;

    /** Longer summary for detail view (max 512 chars) */
    @Column(name = "LONG_SUMMARY", length = 512)
    private String longSummary;

    /** When this alert expires and can be cleaned up */
    @Column(name = "EXPIRATION_DATE")
    private LocalDate expirationDate;

    /** URL for punch-out navigation from the alert */
    @Column(name = "PUNCHOUT_URL", length = 1024)
    private String punchoutUrl;

    // ── Extension: String Attributes (1-20) ──

    @Column(name = "STRING_ATTRIBUTE1", length = 255)
    private String stringAttribute1;

    @Column(name = "STRING_ATTRIBUTE2", length = 255)
    private String stringAttribute2;

    @Column(name = "STRING_ATTRIBUTE3", length = 255)
    private String stringAttribute3;

    @Column(name = "STRING_ATTRIBUTE4", length = 255)
    private String stringAttribute4;

    @Column(name = "STRING_ATTRIBUTE5", length = 255)
    private String stringAttribute5;

    @Column(name = "STRING_ATTRIBUTE6", length = 64)
    private String stringAttribute6;

    @Column(name = "STRING_ATTRIBUTE7", length = 64)
    private String stringAttribute7;

    @Column(name = "STRING_ATTRIBUTE8", length = 64)
    private String stringAttribute8;

    @Column(name = "STRING_ATTRIBUTE9", length = 64)
    private String stringAttribute9;

    @Column(name = "STRING_ATTRIBUTE10", length = 64)
    private String stringAttribute10;

    @Column(name = "STRING_ATTRIBUTE11", length = 64)
    private String stringAttribute11;

    @Column(name = "STRING_ATTRIBUTE12", length = 64)
    private String stringAttribute12;

    @Column(name = "STRING_ATTRIBUTE13", length = 64)
    private String stringAttribute13;

    @Column(name = "STRING_ATTRIBUTE14", length = 64)
    private String stringAttribute14;

    @Column(name = "STRING_ATTRIBUTE15", length = 64)
    private String stringAttribute15;

    @Column(name = "STRING_ATTRIBUTE16", length = 64)
    private String stringAttribute16;

    @Column(name = "STRING_ATTRIBUTE17", length = 64)
    private String stringAttribute17;

    @Column(name = "STRING_ATTRIBUTE18", length = 64)
    private String stringAttribute18;

    @Column(name = "STRING_ATTRIBUTE19", length = 64)
    private String stringAttribute19;

    @Column(name = "STRING_ATTRIBUTE20", length = 64)
    private String stringAttribute20;

    // ── Extension: Date Attributes (1-10) ──

    @Column(name = "DATE_ATTRIBUTE1") private LocalDate dateAttribute1;
    @Column(name = "DATE_ATTRIBUTE2") private LocalDate dateAttribute2;
    @Column(name = "DATE_ATTRIBUTE3") private LocalDate dateAttribute3;
    @Column(name = "DATE_ATTRIBUTE4") private LocalDate dateAttribute4;
    @Column(name = "DATE_ATTRIBUTE5") private LocalDate dateAttribute5;
    @Column(name = "DATE_ATTRIBUTE6") private LocalDate dateAttribute6;
    @Column(name = "DATE_ATTRIBUTE7") private LocalDate dateAttribute7;
    @Column(name = "DATE_ATTRIBUTE8") private LocalDate dateAttribute8;
    @Column(name = "DATE_ATTRIBUTE9") private LocalDate dateAttribute9;
    @Column(name = "DATE_ATTRIBUTE10") private LocalDate dateAttribute10;

    // ── Extension: Numeric Attributes (1-10) ──

    @Column(name = "NUMERIC_ATTRIBUTE1") private Double numericAttribute1;
    @Column(name = "NUMERIC_ATTRIBUTE2") private Double numericAttribute2;
    @Column(name = "NUMERIC_ATTRIBUTE3") private Double numericAttribute3;
    @Column(name = "NUMERIC_ATTRIBUTE4") private Double numericAttribute4;
    @Column(name = "NUMERIC_ATTRIBUTE5") private Double numericAttribute5;
    @Column(name = "NUMERIC_ATTRIBUTE6") private Double numericAttribute6;
    @Column(name = "NUMERIC_ATTRIBUTE7") private Double numericAttribute7;
    @Column(name = "NUMERIC_ATTRIBUTE8") private Double numericAttribute8;
    @Column(name = "NUMERIC_ATTRIBUTE9") private Double numericAttribute9;
    @Column(name = "NUMERIC_ATTRIBUTE10") private Double numericAttribute10;
}


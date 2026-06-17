/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.audit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing audit history records for PCM operations.
 * Tracks all changes made by users with details about what was changed and when.
 */
@Entity
@Table(name = "PCM_AUDIT_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PcmAuditHistory {

	/**
	 * UUID-based primary key for audit record
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "AUDIT_RECORD_KEY")
	private String key;

	/**
	 * Timestamp when the action was performed (database-generated, read-only)
	 */
	@Column(name = "ACTION_DATE", insertable = false, updatable = false)
	private Timestamp actionDate;

	/**
	 * Sequence order for audit records within the same timestamp (database-generated, read-only)
	 */
	@Column(name = "ACTION_ORDER", insertable = false, updatable = false)
	private Long actionOrder;

	/**
	 * Description of the action performed (e.g., "CREATE", "UPDATE", "DELETE", "SAVE")
	 */
	@Column(name = "ACTION_PERFORMED")
	private String actionPerformed;

	/**
	 * User ID who performed the action
	 */
	@Column(name = "USER_ID")
	private String userId;

	/**
	 * Role ID of the user who performed the action
	 */
	@Column(name = "ROLE_ID")
	private String roleId;

	/**
	 * Type of the primary target entity (e.g., "PcmRebateProgram", "Bom", "Item")
	 */
	@Column(name = "PCM_TARGET_TYPE")
	private String targetType;

	/**
	 * Sub-target key for nested/related entity changes
	 */
	@Column(name = "PCM_SUB_TARGET_KEY")
	private String subTargetKey;

	/**
	 * Type of the sub-target entity
	 */
	@Column(name = "PCM_SUB_TARGET_TYPE")
	private String subTargetType;

	/**
	 * Detailed comment/message about the action
	 */
	@Column(name = "ACTION_COMMENT")
	private String comment;

	/**
	 * User who loaded/extracted this record
	 */
	@Column(name = "LAST_LOADED_BY_USER")
	private String lastLoadedByUser;

	/**
	 * List of target entity keys affected by this action
	 * Stored in the PCM_AUDIT_HISTORY_TARGETS junction table
	 */
	@ElementCollection
	@JoinTable(
		name = "PCM_AUDIT_HISTORY_TARGETS",
		joinColumns = @JoinColumn(name = "AUDIT_RECORD_KEY")
	)
	@Column(name = "PCM_TARGET_KEY")
	@Builder.Default
	private List<String> targetKeys = new ArrayList<>();
}

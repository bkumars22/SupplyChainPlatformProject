/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.allocationAudit.entity;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.sql.Timestamp;

/**
 * Audit history for FunctionalGroup upload operations.
 * Discriminator value "FG" written to {@code FG_AUDIT_HISTORY.AUDIT_SOURCE}.
 */
@Entity
@DiscriminatorValue("FG")
public class FunctionalGroupAuditHistory extends AllocationAuditHistory {

    public static final String OPERATION_CREATEFG   = "CREATEFG";
    public static final String OPERATION_RENAMEFG   = "RENAMEFG";
    public static final String OPERATION_UPDATEFG   = "UPDATEFG";
    public static final String OPERATION_ADDITEM    = "ADDITEM";
    public static final String OPERATION_REMOVEITEM = "REMOVEITEM";
    public static final String OPERATION_DEACTIVATE = "DEACTIVATE";
    public static final String OPERATION_ADDPFG     = "ADDPFG";
    public static final String OPERATION_REMOVEPFG  = "REMOVEPFG";


    public FunctionalGroupAuditHistory() {
        super();
    }

    public FunctionalGroupAuditHistory(String userId, String userRole,
            String actionPerformed, String operationCode,
            FunctionalGroup functionalGroup, String comment, Timestamp datePerformed) {
        super(userId, userRole, actionPerformed, operationCode, functionalGroup, comment, datePerformed);
    }

    public FunctionalGroupAuditHistory(String userId, String userRole,
            String actionPerformed, String operationCode,
            FunctionalGroup functionalGroup, Item item, String comment, Timestamp datePerformed) {
        super(userId, userRole, actionPerformed, operationCode, functionalGroup, item, comment, datePerformed);
    }
}

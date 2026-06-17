/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.common;

/**
 * Shared constants for inter-component communication (E2NA, MessageLoader, etc.).
 * Migrated from {@code com.scplatform.utility.common.util.InterconnectConstants}.
 */
public interface InterconnectConstants {

    String SCPLATFORM_ACTION                         = "scplatform.action";
    String SCPLATFORM_MESSAGE                        = "dataType";
    String SCPLATFORM_DATE                           = "scplatform.date";
    String SCPLATFORM_DATE_FORMAT                    = "scplatform.dateFormat";
    String SCPLATFORM_VALIDATE                       = "scplatform.validate";
    String SCPLATFORM_COUNT                          = "scplatform.count";
    String SCPLATFORM_STAT_PREFIX                    = "scplatform.statistic.";
    String SCPLATFORM_STATS                          = "scplatform.statistics";
    String SCPLATFORM_FILETYPE                       = "scplatform.fileType";

    /** Legacy typo in original constant â€” preserved intentionally. */
    String SCPLATFORM_ORIGIN                         = "scplatform.orgin";

    String SCPLATFORM_VALIDATION_BASE                = "scplatform.loader.validation";
    String SCPLATFORM_SOFTERRORS_BASE                = "scplatform.loader.softErrors";
    String SCPLATFORM_ACCESS_CONTROL_ENABLED         = "scplatform.loader.checkAccess";
    String SCPLATFORM_CONTINUE_ON_ERROR_BASE         = "scplatform.loader.continueOnError";
    String SCPLATFORM_WRITE_AUDIT_RECORD_BASE        = "scplatform.loader.writeAuditRecord";
    String SCPLATFORM_WRITE_AUDIT_RECORD_BATCH_BASE  = "scplatform.batch.writeAuditRecord";
    String SCPLATFORM_VERSION_BASE                   = "scplatform.loader.versionOnLoad";
    String SCPLATFORM_USERID                         = "scplatform.userId";
    String SCPLATFORM_QUERY_LIMIT                    = "scplatform.queryLimit";
    String SCPLATFORM_ELEMENT_LIMIT                  = "scplatform.elementLimit";
    String SCPLATFORM_DEFAULT_DATASOURCE             = "scplatform.datasource";

    /** Legacy typo in original constant â€” preserved intentionally. */
    String SCPLATFORM_READONLY_DATASOURCES           = "scplatform.readony.datasources";

    String SCPLATFORM_REPLACE                        = "scplatform.replace";
    String SCPLATFORM_TRANSACTION_TIMEOUT            = "scplatform.transaction.timeout";
    String SCPLATFORM_READONLY_BUSINESS_IDS          = "scplatform.readonly.businesses";
    String SCPLATFORM_STATUS                         = "scplatform.status";
    String SCPLATFORM_EXISTING_LOAD_JOB              = "scplatform.existingLoadJob";
    String SCPLATFORM_HOST_NAMES                     = "scplatform.hostnames";
    String E2NA_HOST_NAMES                      = "e2na.hostnames";
    String E2NA_ALIAS                           = "e2na.alias";
    String SCPLATFORM_FORECAST_ROLLOVER_INTERVAL     = "scplatform.forecast_rollover_interval";
    String SCPLATFORM_FORECAST_AUTOCLOSE_INTERVAL    = "scplatform.forecast_autoclose_interval";
    String SCPLATFORM_FORECAST_AUTOAPPROVE_INTERVAL  = "scplatform.forecast_autoapprove_interval";
    String SCPLATFORM_FORECAST_AUTOCLOSE_ACTION      = "scplatform.forecast_autoclose_action";
    String SCPLATFORM_FORECAST_TYPE                  = "scplatform.forecast_type";
    String SCPLATFORM_ALERT_TYPE                     = "scplatform.alertType";
    String SCPLATFORM_UNLOAD_TYPE                    = "scplatform.unloadType";
    String E2CC_NOTIFICATION_CLIENT_ID          = "scplatform.e2cc.notification.client.id";
    String E2CC_NOTIFICATION_ID                 = "scplatform.e2cc.notification.id";
    String SCPLATFORM_B2BSOURCE                      = "scplatform.b2bsource";
    String SCPLATFORM_TABLE_NAME                     = "scplatform.table.name";
    String SCPLATFORM_KEY_COLUMN_NAME                = "scplatform.key.column.name";
    String SCPLATFORM_SL_COSTTYPE                    = "scplatform.sl_costtype";
    String SCPLATFORM_RECORD_SOURCE_IN               = "scplatform.recordsource.in";
    String SCPLATFORM_RECORD_SOURCE_NOT_IN           = "scplatform.recordsource.not.in";
}

-- ============================================================
-- SCPlatform Alert System - NEW Tables Only
-- These tables DO NOT already exist in the database.
--
-- EXISTING tables (no changes needed):
--   PCM_ALERT_SUBSCRIPTION        → already in create_mcm_model.sql
--   PCM_ALERT_SUBSCRIPTION_OPTIONS → already in create_mcm_model.sql
--   SC_ALERT_DETAIL            → already in create_mcm_model.sql (stores ALL alerts)
--   PCM_USER_ALERTS               → already in create_mcm_model.sql
--
-- NEW tables created here:
--   SC_ALERT_DLQ       → Dead Letter Queue error records
--
-- NOTE: No separate template table needed. Alert summaries are
--       generated in Java by AlertTemplateService using AlertTypes
--       enum descriptions and AlertEvent metadata. All alert data
--       is stored directly in SC_ALERT_DETAIL.
-- ============================================================

-- ─────────────────────────────────────────────────────────────
-- 1. SC_ALERT_DLQ
--    Records alert events that exhausted all retry attempts
--    and were moved to the Dead Letter Queue. Used for manual
--    investigation and resolution by operations staff.
-- ─────────────────────────────────────────────────────────────

CREATE SEQUENCE SC_ALERT_DLQ_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE SC_ALERT_DLQ (
    DLQ_KEY             NUMBER(19)      NOT NULL,
    ALERT_EVENT_ID      VARCHAR2(100),
    ALERT_TYPE          VARCHAR2(50),
    OBJECT_KEY          NUMBER(19),
    REFERENCE_KEY       NUMBER(19),
    ACTOR               NUMBER(19),
    DELIVERY_ATTEMPTS   NUMBER(5),
    ERROR_MESSAGE       VARCHAR2(4000),
    EVENT_PAYLOAD       CLOB,
    STATUS              VARCHAR2(20)    DEFAULT 'NEW',
    RESOLVED_BY         VARCHAR2(200),
    RESOLUTION_NOTES    VARCHAR2(2000),
    RECEIVED_DATE       TIMESTAMP       DEFAULT SYSTIMESTAMP,
    RESOLVED_DATE       TIMESTAMP,
    CONSTRAINT PK_ALERT_DLQ PRIMARY KEY (DLQ_KEY),
    CONSTRAINT CHK_DLQ_STATUS CHECK (STATUS IN ('NEW', 'REVIEWED', 'RESOLVED', 'REPLAYED'))
);

CREATE INDEX IDX_ALERT_DLQ_STATUS ON SC_ALERT_DLQ (STATUS);
CREATE INDEX IDX_ALERT_DLQ_EVENT_ID ON SC_ALERT_DLQ (ALERT_EVENT_ID);
CREATE INDEX IDX_ALERT_DLQ_TYPE ON SC_ALERT_DLQ (ALERT_TYPE);
CREATE INDEX IDX_ALERT_DLQ_RECEIVED ON SC_ALERT_DLQ (RECEIVED_DATE);

COMMENT ON TABLE SC_ALERT_DLQ IS 'Dead Letter Queue records for failed alert events requiring manual review';
COMMENT ON COLUMN SC_ALERT_DLQ.EVENT_PAYLOAD IS 'Full JSON serialization of the AlertEvent for potential replay';
COMMENT ON COLUMN SC_ALERT_DLQ.STATUS IS 'NEW=unreviewed, REVIEWED=acknowledged, RESOLVED=fixed, REPLAYED=re-queued';


-- ============================================================
-- Flyway Migration V12 — Supplier ESG Profile
-- Supply Chain Intelligence Platform
-- Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
--
-- Phase 2 of scip_master_plan: ESG/sustainability data, kept in its own
-- table (not columns on SUPPLIER_PROFILE) so this feature never touches
-- that table. Most suppliers won't have a row here initially -- absence
-- of a row (not just null columns) is what SupplierEsgService uses to
-- report "no ESG data on file" rather than fabricating a score.
-- ============================================================

CREATE TABLE IF NOT EXISTS SUPPLIER_ESG_PROFILE (
    SUPPLIER_ID                  VARCHAR(50) PRIMARY KEY REFERENCES SUPPLIER_PROFILE(SUPPLIER_ID),
    ESG_CERTIFICATIONS           VARCHAR(500),   -- comma-separated, e.g. 'ISO14001,SA8000'
    CARBON_INTENSITY_SCORE       NUMERIC(5,2),   -- normalized 0-100, lower is better
    COMPLIANCE_VIOLATIONS_12MO   INTEGER,
    LABOR_AUDIT_SCORE            NUMERIC(5,2)    -- 0-100, higher is better; NULL = no third-party audit on file
);

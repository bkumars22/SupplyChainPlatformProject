-- ============================================================
-- Flyway Migration V11 — Supplier Dependency Graph
-- Supply Chain Intelligence Platform
-- Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
--
-- Suppliers aren't a flat list -- they form a dependency graph (a
-- Tier-1 supplier can depend on a Tier-2 source for a critical
-- component). This table is the edge list for that graph; the
-- cascading risk algorithm (SupplierRiskCascadeService) walks it.
-- ============================================================

CREATE TABLE IF NOT EXISTS SUPPLIER_DEPENDENCY (
    ID                       BIGSERIAL PRIMARY KEY,
    DEPENDENT_SUPPLIER_ID    VARCHAR(50) NOT NULL REFERENCES SUPPLIER_PROFILE(SUPPLIER_ID),
    UPSTREAM_SUPPLIER_ID     VARCHAR(50) NOT NULL REFERENCES SUPPLIER_PROFILE(SUPPLIER_ID),
    COMPONENT_OR_MATERIAL    VARCHAR(255),
    DEPENDENCY_CRITICALITY   NUMERIC(3,2) NOT NULL DEFAULT 0.5 CHECK (DEPENDENCY_CRITICALITY BETWEEN 0 AND 1),
    -- 1.0 = this input is essential, no substitute; 0.3 = minor/substitutable input
    IS_SOLE_SOURCE           BOOLEAN NOT NULL DEFAULT FALSE,
    -- TRUE if DEPENDENT_SUPPLIER_ID has no redundant alternative for this input
    CREATED_AT               TIMESTAMP DEFAULT NOW(),
    CONSTRAINT chk_dependency_not_self CHECK (DEPENDENT_SUPPLIER_ID <> UPSTREAM_SUPPLIER_ID)
);

CREATE INDEX IF NOT EXISTS idx_dep_dependent ON SUPPLIER_DEPENDENCY(DEPENDENT_SUPPLIER_ID);
CREATE INDEX IF NOT EXISTS idx_dep_upstream ON SUPPLIER_DEPENDENCY(UPSTREAM_SUPPLIER_ID);

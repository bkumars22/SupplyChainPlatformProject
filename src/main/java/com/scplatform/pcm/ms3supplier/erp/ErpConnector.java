/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier.erp;

import java.util.List;

/**
 * scip_master_plan Phase 4: "Read-only by design for v1 -- proves the
 * integration concept without the far larger scope of writing back to a
 * client's system of record." One implementation exists today
 * (CsvImportErpConnector); a live API connector (NetSuite, SAP, ...) would
 * be a second implementation, added only once a real prospect asks for one
 * specifically -- the plan explicitly defers that, so this interface has
 * exactly the one implementation it needs right now, not a speculative
 * second one.
 */
public interface ErpConnector {

    /** Supplier master data -- id, name, category. Nothing else yet. */
    List<SupplierRecord> fetchSuppliers();

    /** Delivery/transaction history needed for direct risk scoring. */
    List<TransactionRecord> fetchRecentTransactions(String supplierId);
}

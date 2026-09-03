/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier.erp;

import com.scplatform.pcm.ms3supplier.SupplierCsvImportService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * scip_master_plan Phase 4: "Build this FIRST -- before any live API
 * integration. A generic CSV/Excel importer proves the data model and
 * scoring pipeline work end-to-end against real external data."
 *
 * Wraps SupplierCsvImportService.parseCsv()'s output -- already the
 * hardened, tested parser (30 unit tests, including the real messy-Excel
 * fixture and the UTF-8 BOM fix) -- behind the ErpConnector shape,
 * without touching that service or the upload controller that already
 * uses it in production. This connector operates on one already-parsed
 * snapshot (there's no live external system to re-poll for a CSV, unlike
 * a real API-based ERP), so it takes the parsed rows in its constructor
 * rather than fetching them itself.
 */
public class CsvImportErpConnector implements ErpConnector {

    private final List<SupplierCsvImportService.ParsedRow> rows;

    public CsvImportErpConnector(List<SupplierCsvImportService.ParsedRow> rows) {
        this.rows = rows;
    }

    @Override
    public List<SupplierRecord> fetchSuppliers() {
        Map<String, SupplierRecord> bySupplier = new LinkedHashMap<>();
        for (SupplierCsvImportService.ParsedRow row : rows) {
            if (!row.valid) continue;
            bySupplier.putIfAbsent(row.supplierId, new SupplierRecord(row.supplierId, row.supplierName, null));
        }
        return List.copyOf(bySupplier.values());
    }

    @Override
    public List<TransactionRecord> fetchRecentTransactions(String supplierId) {
        return rows.stream()
            .filter(row -> row.valid && row.supplierId.equals(supplierId))
            .map(row -> new TransactionRecord(row.poNumber, row.promisedDate, row.actualDate, row.qtyOrdered, row.qtyReceived))
            .toList();
    }
}

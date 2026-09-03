/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier.erp;

/**
 * category is nullable -- SCIP's CSV import format (supplier_id,
 * supplier_name, country, po_number, item_code, promised_date,
 * actual_date, qty_ordered, qty_received, quality_score) has no category
 * column, so CsvImportErpConnector always leaves it null rather than
 * mislabeling country as category.
 */
public record SupplierRecord(String supplierId, String supplierName, String category) {
}

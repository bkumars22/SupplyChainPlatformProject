/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier.erp;

import java.time.LocalDate;

public record TransactionRecord(
    String poNumber,
    LocalDate promisedDate,
    LocalDate actualDate,
    int qtyOrdered,
    int qtyReceived
) {
}

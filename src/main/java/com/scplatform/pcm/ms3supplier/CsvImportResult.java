/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.List;

public class CsvImportResult {

    private int importedSuppliers;
    private int importedDeliveries;
    private List<RowError> errors;

    public static class RowError {
        private int row;
        private String supplierId;
        private String message;

        public RowError(int row, String supplierId, String message) {
            this.row = row;
            this.supplierId = supplierId;
            this.message = message;
        }

        public int getRow()           { return row; }
        public String getSupplierId() { return supplierId; }
        public String getMessage()    { return message; }
    }

    public CsvImportResult(int importedSuppliers, int importedDeliveries, List<RowError> errors) {
        this.importedSuppliers  = importedSuppliers;
        this.importedDeliveries = importedDeliveries;
        this.errors             = errors;
    }

    public int getImportedSuppliers()  { return importedSuppliers; }
    public int getImportedDeliveries() { return importedDeliveries; }
    public List<RowError> getErrors()  { return errors; }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier.erp;

import com.scplatform.pcm.ms3supplier.SupplierCsvImportService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CsvImportErpConnectorTest {

    private static List<SupplierCsvImportService.ParsedRow> parseMessyFixture() throws Exception {
        String csv = new String(
            Files.readAllBytes(Paths.get(CsvImportErpConnectorTest.class.getClassLoader()
                .getResource("ms3supplier/messy_supplier_import.csv").toURI())),
            StandardCharsets.UTF_8);
        return new SupplierCsvImportService().parseCsv(csv);
    }

    @Test
    void fetchSuppliers_returnsOneRecordPerDistinctValidSupplier() throws Exception {
        var connector = new CsvImportErpConnector(parseMessyFixture());

        List<SupplierRecord> suppliers = connector.fetchSuppliers();

        // Same 5 distinct suppliers the messy fixture is already known to
        // produce (see SupplierCsvImportServiceTest's real-file test) --
        // SUP-900 appears twice in the file but must collapse to one record.
        assertEquals(Set.of("SUP-900", "SUP-901", "SUP-902", "SUP-903", "SUP-905"),
            suppliers.stream().map(SupplierRecord::supplierId).collect(Collectors.toSet()));

        SupplierRecord meridian = suppliers.stream()
            .filter(s -> s.supplierId().equals("SUP-901")).findFirst().orElseThrow();
        assertEquals("Meridian, Rossi & Co.", meridian.supplierName());
        assertNull(meridian.category(), "CSV format has no category column -- must not be mislabeled");
    }

    @Test
    void fetchRecentTransactions_returnsOnlyThatSuppliersRows() throws Exception {
        var connector = new CsvImportErpConnector(parseMessyFixture());

        List<TransactionRecord> txns = connector.fetchRecentTransactions("SUP-905");

        // SUP-905 has 2 valid rows in the fixture (one with an unexpected
        // trailing column that must still parse).
        assertEquals(2, txns.size());
        assertTrue(txns.stream().allMatch(t -> t.poNumber().startsWith("PO-906")));
    }

    @Test
    void fetchRecentTransactions_unknownSupplier_returnsEmpty() throws Exception {
        var connector = new CsvImportErpConnector(parseMessyFixture());
        assertTrue(connector.fetchRecentTransactions("NO-SUCH-SUPPLIER").isEmpty());
    }

    @Test
    void invalidRowsAreExcludedFromBothMethods() throws Exception {
        var connector = new CsvImportErpConnector(parseMessyFixture());

        // SUP-904's row is invalid (malformed date) -- must not surface anywhere.
        assertTrue(connector.fetchSuppliers().stream().noneMatch(s -> s.supplierId().equals("SUP-904")));
        assertTrue(connector.fetchRecentTransactions("SUP-904").isEmpty());
    }
}

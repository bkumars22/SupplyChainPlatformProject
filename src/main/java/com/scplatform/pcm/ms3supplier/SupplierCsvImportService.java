/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class SupplierCsvImportService {

    // Column header names accepted from the CSV (case-insensitive).
    private static final String COL_SUPPLIER_ID    = "supplier_id";
    private static final String COL_SUPPLIER_NAME  = "supplier_name";
    private static final String COL_COUNTRY        = "country";
    private static final String COL_PO_NUMBER      = "po_number";
    private static final String COL_ITEM_CODE      = "item_code";
    private static final String COL_PROMISED_DATE  = "promised_date";
    private static final String COL_ACTUAL_DATE    = "actual_date";
    private static final String COL_QTY_ORDERED    = "qty_ordered";
    private static final String COL_QTY_RECEIVED   = "qty_received";
    private static final String COL_QUALITY_SCORE  = "quality_score";

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("M/d/yyyy")
    );

    @Autowired private SupplierRepository supplierRepo;
    @Autowired private DeliveryRepository deliveryRepo;

    // ── Public DTO returned by parseCsv ──────────────────────────────────────

    public static class ParsedRow {
        public final int    rowNumber;
        public final String supplierId;
        public final String supplierName;
        public final String country;
        public final String poNumber;
        public final String itemCode;
        public final LocalDate promisedDate;
        public final LocalDate actualDate;
        public final int    qtyOrdered;
        public final int    qtyReceived;
        public final Double qualityScore;
        public final boolean valid;
        public final String errorMessage;

        private ParsedRow(int rowNumber, String supplierId, String supplierName,
                          String country, String poNumber, String itemCode,
                          LocalDate promisedDate, LocalDate actualDate,
                          int qtyOrdered, int qtyReceived, Double qualityScore) {
            this.rowNumber    = rowNumber;
            this.supplierId   = supplierId;
            this.supplierName = supplierName;
            this.country      = country;
            this.poNumber     = poNumber;
            this.itemCode     = itemCode;
            this.promisedDate = promisedDate;
            this.actualDate   = actualDate;
            this.qtyOrdered   = qtyOrdered;
            this.qtyReceived  = qtyReceived;
            this.qualityScore = qualityScore;
            this.valid        = true;
            this.errorMessage = null;
        }

        private ParsedRow(int rowNumber, String supplierId, String errorMessage) {
            this.rowNumber    = rowNumber;
            this.supplierId   = supplierId;
            this.supplierName = null;
            this.country      = null;
            this.poNumber     = null;
            this.itemCode     = null;
            this.promisedDate = null;
            this.actualDate   = null;
            this.qtyOrdered   = 0;
            this.qtyReceived  = 0;
            this.qualityScore = null;
            this.valid        = false;
            this.errorMessage = errorMessage;
        }
    }

    // ── Pure CSV parsing — no DB access, fully unit-testable ─────────────────

    public List<ParsedRow> parseCsv(String csvContent) {
        if (csvContent == null || csvContent.isBlank()) {
            return Collections.emptyList();
        }

        List<ParsedRow> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {

            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                return Collections.emptyList();
            }

            Map<String, Integer> colIndex = buildColumnIndex(parseLine(headerLine));

            String line;
            int rowNum = 2; // data rows start at row 2 (1-based, row 1 = header)
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) { rowNum++; continue; }
                result.add(parseDataRow(rowNum, parseLine(line), colIndex));
                rowNum++;
            }
        } catch (IOException e) {
            // BufferedReader on StringReader never throws checked IOException
            throw new IllegalStateException("Unexpected I/O error reading CSV string", e);
        }
        return result;
    }

    // ── Import: parse → validate → upsert ────────────────────────────────────

    @Transactional
    public CsvImportResult importCsv(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new CsvImportResult(0, 0,
                List.of(new CsvImportResult.RowError(0, null, "Uploaded file is empty.")));
        }

        String content = new String(file.getBytes(), StandardCharsets.UTF_8);
        List<ParsedRow> rows = parseCsv(content);

        if (rows.isEmpty()) {
            return new CsvImportResult(0, 0,
                List.of(new CsvImportResult.RowError(0, null,
                    "File contains no data rows (header only or completely empty).")));
        }

        List<CsvImportResult.RowError> errors = new ArrayList<>();
        Set<String> upsertedSuppliers = new HashSet<>();
        int deliveryCount = 0;

        for (ParsedRow row : rows) {
            if (!row.valid) {
                errors.add(new CsvImportResult.RowError(row.rowNumber, row.supplierId, row.errorMessage));
                continue;
            }

            // Upsert SupplierProfile — create on first occurrence, skip updates to avoid
            // overwriting data already enriched by the scoring pipeline.
            if (!upsertedSuppliers.contains(row.supplierId)) {
                Optional<SupplierProfile> existing = supplierRepo.findById(row.supplierId);
                if (existing.isEmpty()) {
                    SupplierProfile profile = new SupplierProfile();
                    profile.setSupplierId(row.supplierId);
                    profile.setSupplierName(row.supplierName);
                    profile.setCountry(row.country);
                    profile.setTier(SupplierTier.APPROVED);
                    profile.setIsActive(true);
                    if (row.qualityScore != null) {
                        profile.setQualityScore(row.qualityScore);
                    }
                    supplierRepo.save(profile);
                }
                upsertedSuppliers.add(row.supplierId);
            }

            SupplierProfile profile = supplierRepo.findById(row.supplierId).orElseThrow();

            SupplierDelivery delivery = new SupplierDelivery();
            delivery.setSupplier(profile);
            delivery.setPoNumber(row.poNumber);
            delivery.setItemCode(row.itemCode);
            delivery.setPromisedDate(row.promisedDate);
            delivery.setActualDate(row.actualDate);
            delivery.setQtyOrdered(row.qtyOrdered);
            delivery.setQtyReceived(row.qtyReceived);

            long delayDays = java.time.temporal.ChronoUnit.DAYS.between(row.promisedDate, row.actualDate);
            delivery.setDelayDays((int) delayDays);
            delivery.setStatus(delayDays <= 0 ? DeliveryStatus.ON_TIME : DeliveryStatus.LATE);

            deliveryRepo.save(delivery);
            deliveryCount++;
        }

        return new CsvImportResult(upsertedSuppliers.size(), deliveryCount, errors);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private ParsedRow parseDataRow(int rowNum, List<String> fields, Map<String, Integer> colIndex) {
        String supplierId  = get(fields, colIndex, COL_SUPPLIER_ID);
        String supplierName = get(fields, colIndex, COL_SUPPLIER_NAME);
        String promisedRaw = get(fields, colIndex, COL_PROMISED_DATE);
        String actualRaw   = get(fields, colIndex, COL_ACTUAL_DATE);

        // ── Required field validation ──
        if (supplierId.isEmpty()) {
            return new ParsedRow(rowNum, null, "Missing required field: supplier_id");
        }
        if (supplierName.isEmpty()) {
            return new ParsedRow(rowNum, supplierId, "Missing required field: supplier_name");
        }
        if (promisedRaw.isEmpty()) {
            return new ParsedRow(rowNum, supplierId, "Missing required field: promised_date");
        }
        if (actualRaw.isEmpty()) {
            return new ParsedRow(rowNum, supplierId, "Missing required field: actual_date");
        }

        // ── Date parsing ──
        LocalDate promisedDate = parseDate(promisedRaw);
        if (promisedDate == null) {
            return new ParsedRow(rowNum, supplierId,
                "Invalid promised_date '" + promisedRaw + "' — expected YYYY-MM-DD (e.g. 2024-01-15)");
        }
        LocalDate actualDate = parseDate(actualRaw);
        if (actualDate == null) {
            return new ParsedRow(rowNum, supplierId,
                "Invalid actual_date '" + actualRaw + "' — expected YYYY-MM-DD (e.g. 2024-01-17)");
        }

        // ── Optional fields with forgiving defaults ──
        String country  = get(fields, colIndex, COL_COUNTRY);
        String poNumber = get(fields, colIndex, COL_PO_NUMBER);
        if (poNumber.isEmpty()) poNumber = "CSV-" + rowNum;
        String itemCode = get(fields, colIndex, COL_ITEM_CODE);
        if (itemCode.isEmpty()) itemCode = "IMPORTED";

        int qtyOrdered = parseIntOrDefault(get(fields, colIndex, COL_QTY_ORDERED), 1);
        int qtyReceived = parseIntOrDefault(get(fields, colIndex, COL_QTY_RECEIVED), qtyOrdered);

        String qualityRaw = get(fields, colIndex, COL_QUALITY_SCORE);
        Double qualityScore = qualityRaw.isEmpty() ? null : parseDoubleOrNull(qualityRaw);

        return new ParsedRow(rowNum, supplierId, supplierName, country, poNumber, itemCode,
                             promisedDate, actualDate, qtyOrdered, qtyReceived, qualityScore);
    }

    private Map<String, Integer> buildColumnIndex(List<String> headers) {
        Map<String, Integer> index = new LinkedHashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            index.put(headers.get(i).toLowerCase(Locale.ROOT).trim(), i);
        }
        return index;
    }

    private String get(List<String> fields, Map<String, Integer> colIndex, String col) {
        Integer idx = colIndex.get(col);
        if (idx == null || idx >= fields.size()) return "";
        return fields.get(idx).trim();
    }

    // Handles quoted fields and escaped quotes ("") per RFC 4180.
    static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString().trim());
        return fields;
    }

    static LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(raw.trim(), fmt);
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }

    private static int parseIntOrDefault(String raw, int defaultValue) {
        try { return Integer.parseInt(raw); } catch (NumberFormatException e) { return defaultValue; }
    }

    private static Double parseDoubleOrNull(String raw) {
        try { return Double.parseDouble(raw); } catch (NumberFormatException e) { return null; }
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/suppliers/import")
@Tag(name = "Supplier CSV Import", description = "Manual CSV onboarding for small-business users without ERP/API integration")
public class SupplierCsvUploadController extends BaseApiController {

    private static final String TEMPLATE_HEADER =
        "supplier_id,supplier_name,country,po_number,item_code,promised_date,actual_date,qty_ordered,qty_received,quality_score";

    private static final String TEMPLATE_EXAMPLE_ROW =
        "SUPP-001,Acme Supplies,USA,PO-2024-001,WIDGET-A,2024-01-15,2024-01-17,100,100,5";

    private static final String TEMPLATE_NOTES =
        "# Notes:\n" +
        "# Required columns: supplier_id  supplier_name  promised_date  actual_date\n" +
        "# Optional columns (leave blank for defaults): country  po_number  item_code  qty_ordered  qty_received  quality_score\n" +
        "# quality_score = defect rate % (0=no defects, 100=all defective). Defaults to 0 if blank.\n" +
        "# Dates must be YYYY-MM-DD format (e.g. 2024-01-15). MM/DD/YYYY is also accepted.\n" +
        "# Each row represents one delivery/shipment event for that supplier.\n" +
        "# supplier_id must be unique per supplier — use the same ID across multiple rows for the same supplier.\n";

    @Autowired
    private SupplierCsvImportService importService;

    @GetMapping("/template")
    @Operation(summary = "Download a pre-filled CSV template with the required columns and one example row")
    public ResponseEntity<byte[]> downloadTemplate() {
        String csv = TEMPLATE_NOTES + "\n" + TEMPLATE_HEADER + "\n" + TEMPLATE_EXAMPLE_ROW + "\n";
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=supplier_import_template.csv");
        headers.setContentLength(bytes.length);

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a CSV of supplier delivery history — creates supplier profiles and delivery records")
    public ResponseEntity<ApiResponse<CsvImportResult>> uploadCsv(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return badRequest("No file received. Please attach a CSV file.");
        }

        String filename = file.getOriginalFilename();
        if (filename != null && !filename.toLowerCase().endsWith(".csv")) {
            return badRequest("Only .csv files are accepted.");
        }

        try {
            CsvImportResult result = importService.importCsv(file);
            String message = result.getImportedDeliveries() + " deliveries imported for "
                + result.getImportedSuppliers() + " supplier(s)."
                + (result.getErrors().isEmpty() ? "" : " " + result.getErrors().size() + " row(s) had errors.");
            return ok(result);
        } catch (IOException e) {
            return badRequest("Could not read the uploaded file: " + e.getMessage());
        }
    }
}

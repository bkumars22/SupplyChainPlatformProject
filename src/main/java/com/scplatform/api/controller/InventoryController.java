/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import com.scplatform.pcm.inventory.InventoryItem;
import com.scplatform.pcm.inventory.InventoryService;
import com.scplatform.pcm.inventory.InventoryTransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory", description = "Inventory item management — stock levels, transactions, adjustments")
public class InventoryController extends BaseApiController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory items with optional warehouse and low-stock filters")
    public ResponseEntity<ApiResponse<List<InventoryItem>>> getAllItems(
            @RequestParam(required = false) String warehouse,
            @RequestParam(required = false) Boolean lowStock) {
        return ok(inventoryService.getAllItems(warehouse, lowStock));
    }

    @GetMapping("/{itemKey}/transactions")
    @Operation(summary = "Get all transactions for a specific inventory item")
    public ResponseEntity<ApiResponse<List<InventoryTransaction>>> getTransactions(
            @PathVariable String itemKey) {
        return ok(inventoryService.getTransactions(itemKey));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust inventory stock — IN, OUT, or ADJUSTMENT")
    public ResponseEntity<ApiResponse<InventoryTransaction>> adjustStock(
            @RequestBody InventoryService.AdjustRequest req) {
        return created(inventoryService.adjustStock(req));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export all inventory items as CSV")
    public ResponseEntity<byte[]> exportCsv() {
        List<InventoryItem> items = inventoryService.getAllItems(null, null);

        StringBuilder csv = new StringBuilder();
        csv.append("Item Key,Item Name,Warehouse ID,Current Stock,Reorder Point,Max Stock,UOM,Category,Last Updated\n");

        for (InventoryItem item : items) {
            csv.append(escapeCsv(item.getItemKey())).append(",")
               .append(escapeCsv(item.getItemName())).append(",")
               .append(escapeCsv(item.getWarehouseId())).append(",")
               .append(item.getCurrentStock() != null ? item.getCurrentStock().toPlainString() : "0").append(",")
               .append(item.getReorderPoint() != null ? item.getReorderPoint().toPlainString() : "0").append(",")
               .append(item.getMaxStock() != null ? item.getMaxStock().toPlainString() : "0").append(",")
               .append(escapeCsv(item.getUom())).append(",")
               .append(escapeCsv(item.getCategory())).append(",")
               .append(item.getLastUpdated() != null ? item.getLastUpdated().toString() : "")
               .append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.csv");
        headers.setContentLength(csvBytes.length);

        return ResponseEntity.ok().headers(headers).body(csvBytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import com.scplatform.pcm.purchaseorder.POLineItem;
import com.scplatform.pcm.purchaseorder.PurchaseOrder;
import com.scplatform.pcm.purchaseorder.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@Tag(name = "Purchase Orders", description = "Purchase order lifecycle management — create, submit, confirm, receive")
public class PurchaseOrderController extends BaseApiController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @GetMapping
    @Operation(summary = "Get all purchase orders with optional search and status filter")
    public ResponseEntity<ApiResponse<List<PurchaseOrder>>> getAllOrders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ok(purchaseOrderService.getAllOrders(search, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a purchase order by ID")
    public ResponseEntity<ApiResponse<PurchaseOrder>> getOrder(@PathVariable Long id) {
        return ok(purchaseOrderService.getOrder(id));
    }

    @PostMapping
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrder>> createOrder(
            @RequestBody PurchaseOrderService.CreatePORequest req) {
        return created(purchaseOrderService.createOrder(req));
    }

    @PutMapping("/{id}/submit")
    @Operation(summary = "Submit a draft purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrder>> submitOrder(@PathVariable Long id) {
        return ok(purchaseOrderService.submitOrder(id));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm a submitted purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrder>> confirmOrder(@PathVariable Long id) {
        return ok(purchaseOrderService.confirmOrder(id));
    }

    @PutMapping("/{id}/receive")
    @Operation(summary = "Mark a confirmed purchase order as received")
    public ResponseEntity<ApiResponse<PurchaseOrder>> receiveOrder(@PathVariable Long id) {
        return ok(purchaseOrderService.receiveOrder(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a draft or submitted purchase order")
    public ResponseEntity<ApiResponse<PurchaseOrder>> cancelOrder(@PathVariable Long id) {
        return ok(purchaseOrderService.cancelOrder(id));
    }

    @PostMapping("/{id}/line-items")
    @Operation(summary = "Add a line item to a purchase order")
    public ResponseEntity<ApiResponse<POLineItem>> addLineItem(
            @PathVariable Long id,
            @RequestBody PurchaseOrderService.LineItemRequest req) {
        return created(purchaseOrderService.addLineItem(id, req));
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Export all purchase orders as CSV")
    public ResponseEntity<byte[]> exportCsv() {
        List<PurchaseOrder> orders = purchaseOrderService.getAllOrders(null, null);

        StringBuilder csv = new StringBuilder();
        csv.append("PO Number,Supplier ID,Status,Order Date,Expected Date,Received Date,Total Amount,Currency,Created By\n");

        for (PurchaseOrder po : orders) {
            csv.append(escapeCsv(po.getPoNumber())).append(",")
               .append(escapeCsv(po.getSupplierId())).append(",")
               .append(escapeCsv(po.getStatus())).append(",")
               .append(po.getOrderDate() != null ? po.getOrderDate().toString() : "").append(",")
               .append(po.getExpectedDate() != null ? po.getExpectedDate().toString() : "").append(",")
               .append(po.getReceivedDate() != null ? po.getReceivedDate().toString() : "").append(",")
               .append(po.getTotalAmount() != null ? po.getTotalAmount().toPlainString() : "0").append(",")
               .append(escapeCsv(po.getCurrency())).append(",")
               .append(escapeCsv(po.getCreatedBy()))
               .append("\n");
        }

        byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=purchase-orders.csv");
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

/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.inventory;

import com.scplatform.pcm.alert.entity.Alert;
import com.scplatform.pcm.alert.repository.AlertRepository;
import com.scplatform.pcm.auditlog.Auditable;
import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryItemRepository itemRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private AlertRepository alertRepository;

    public List<InventoryItem> getAllItems(String warehouse, Boolean lowStockOnly) {
        if (Boolean.TRUE.equals(lowStockOnly)) {
            List<InventoryItem> lowStock = itemRepository.findLowStockItems();
            if (warehouse != null && !warehouse.isBlank()) {
                return lowStock.stream()
                        .filter(i -> warehouse.equals(i.getWarehouseId()))
                        .toList();
            }
            return lowStock;
        }

        if (warehouse != null && !warehouse.isBlank()) {
            return itemRepository.findByWarehouseIdOrderByItemKeyAsc(warehouse);
        }

        return itemRepository.findAll();
    }

    public List<InventoryTransaction> getTransactions(String itemKey) {
        return transactionRepository.findByItemKeyOrderByTransactionDateDesc(itemKey);
    }

    @Auditable(entityType = "InventoryItem", action = "ADJUST_STOCK")
    public InventoryTransaction adjustStock(AdjustRequest req) {
        InventoryItem item = itemRepository.findByItemKey(req.itemKey())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + req.itemKey()));

        BigDecimal currentStock = item.getCurrentStock() != null ? item.getCurrentStock() : BigDecimal.ZERO;
        BigDecimal newStock;
        String type = req.transactionType().toUpperCase();

        switch (type) {
            case "IN" -> newStock = currentStock.add(req.quantity());
            case "OUT" -> {
                newStock = currentStock.subtract(req.quantity());
                if (newStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Insufficient stock. Current: " + currentStock + ", Requested: " + req.quantity());
                }
            }
            case "ADJUSTMENT" -> newStock = currentStock.add(req.quantity()); // quantity can be negative for downward adjustment
            default -> throw new IllegalArgumentException("Invalid transaction type: " + req.transactionType() + ". Must be IN, OUT, or ADJUSTMENT");
        }

        item.setCurrentStock(newStock);
        item.setLastUpdated(LocalDateTime.now());
        itemRepository.save(item);

        InventoryTransaction tx = new InventoryTransaction();
        tx.setItemKey(req.itemKey());
        tx.setTransactionType(type);
        tx.setQuantity(req.quantity());
        tx.setBalanceAfter(newStock);
        tx.setReference(req.reference());
        tx.setNotes(req.notes());
        tx.setPerformedBy(req.performedBy());
        tx.setTransactionDate(LocalDateTime.now());

        InventoryTransaction saved = transactionRepository.save(tx);

        // Low-stock alert — only for OUT/ADJUSTMENT that reduces stock
        if (("OUT".equals(type) || "ADJUSTMENT".equals(type)) && newStock.compareTo(BigDecimal.ZERO) >= 0) {
            BigDecimal reorder = item.getReorderPoint() != null ? item.getReorderPoint() : BigDecimal.ZERO;
            if (newStock.compareTo(reorder) <= 0) {
                createLowStockAlert(item, newStock, reorder, req.performedBy());
            }
        }

        return saved;
    }

    private void createLowStockAlert(InventoryItem item, BigDecimal newStock, BigDecimal reorder, String performedBy) {
        // Avoid duplicate alerts — check if an unresolved ACTIVE alert exists for this item
        String alertType = "InventoryLowStock";
        String itemKey   = item.getItemKey();
        boolean existing = alertRepository.findAll().stream()
                .anyMatch(a -> alertType.equals(a.getAlertType())
                        && itemKey.equals(a.getStringAttribute1())
                        && Alert.ACTIVE.equals(a.getState()));
        if (existing) return;

        boolean critical  = newStock.compareTo(BigDecimal.ZERO) == 0;
        String  itemName  = item.getItemName() != null ? item.getItemName() : itemKey;
        String  uom       = item.getUom() != null ? item.getUom() : "units";
        String  message   = "Low stock alert: " + itemName + " has only "
                + newStock.toPlainString() + " " + uom
                + " remaining. Reorder point is " + reorder.toPlainString() + ".";

        Alert alert = new Alert();
        alert.setState(Alert.ACTIVE);
        alert.setAlertId("Inventory-" + (critical ? "CRITICAL" : "LOW") + "-" + itemKey + "-" + System.currentTimeMillis());
        alert.setAlertType(alertType);
        alert.setAlertLabel(critical ? "CRITICAL: Stock depleted" : "Low Stock Warning");
        alert.setShortSummary(message);
        alert.setLongSummary(message + (critical ? " CRITICAL — stock is zero." : " Severity: MEDIUM."));
        alert.setStringAttribute1(itemKey);
        alert.setUserId(performedBy);
        alert.setCreated(new Date());
        alert.setExpirationDate(java.sql.Date.valueOf(LocalDate.now().plusDays(7)));
        alertRepository.save(alert);
        log.info("Low-stock alert created for item '{}' — stock: {}", itemKey, newStock);
    }

    public record AdjustRequest(
            String itemKey,
            String transactionType,
            BigDecimal quantity,
            String reference,
            String notes,
            String performedBy
    ) {}
}

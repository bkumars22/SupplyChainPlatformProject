/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.purchaseorder;

import com.scplatform.pcm.auditlog.Auditable;
import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import com.scplatform.pcm.inventory.InventoryItem;
import com.scplatform.pcm.inventory.InventoryItemRepository;
import com.scplatform.pcm.inventory.InventoryTransaction;
import com.scplatform.pcm.inventory.InventoryTransactionRepository;
import com.scplatform.pcm.ms3cost.CostRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderService {

    private static final Logger log = LoggerFactory.getLogger(PurchaseOrderService.class);

    @Autowired
    private PurchaseOrderRepository poRepository;

    @Autowired
    private POLineItemRepository lineItemRepository;

    @Autowired
    private CostRecordService costRecordService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    public List<PurchaseOrder> getAllOrders(String search, String status) {
        List<PurchaseOrder> orders;

        if (search != null && !search.isBlank()) {
            orders = poRepository.findByPoNumberContainingIgnoreCaseOrderByCreatedAtDesc(search);
        } else if (status != null && !status.isBlank()) {
            orders = poRepository.findByStatus(status.toUpperCase());
        } else {
            orders = poRepository.findAll();
        }

        return orders;
    }

    public PurchaseOrder getOrder(Long id) {
        return poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found: " + id));
    }

    @Auditable(entityType = "PurchaseOrder", action = "CREATE")
    public PurchaseOrder createOrder(CreatePORequest req) {
        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber(generatePoNumber());
        po.setSupplierId(req.supplierId());
        po.setStatus("DRAFT");
        po.setOrderDate(LocalDate.now());
        po.setExpectedDate(req.expectedDate());
        po.setCurrency(req.currency() != null ? req.currency() : "USD");
        po.setNotes(req.notes());
        po.setCreatedBy(req.createdBy());
        po.setTotalAmount(BigDecimal.ZERO);
        po.setCreatedAt(LocalDateTime.now());
        po.setUpdatedAt(LocalDateTime.now());
        return poRepository.save(po);
    }

    @Auditable(entityType = "PurchaseOrder", action = "SUBMIT")
    public PurchaseOrder submitOrder(Long id) {
        PurchaseOrder po = getOrder(id);
        if (!"DRAFT".equals(po.getStatus())) {
            throw new IllegalStateException("Only DRAFT orders can be submitted. Current status: " + po.getStatus());
        }
        po.setStatus("SUBMITTED");
        po.setUpdatedAt(LocalDateTime.now());
        return poRepository.save(po);
    }

    @Auditable(entityType = "PurchaseOrder", action = "CONFIRM")
    public PurchaseOrder confirmOrder(Long id) {
        PurchaseOrder po = getOrder(id);
        if (!"SUBMITTED".equals(po.getStatus())) {
            throw new IllegalStateException("Only SUBMITTED orders can be confirmed. Current status: " + po.getStatus());
        }
        po.setStatus("CONFIRMED");
        po.setUpdatedAt(LocalDateTime.now());
        return poRepository.save(po);
    }

    @Auditable(entityType = "PurchaseOrder", action = "RECEIVE")
    public PurchaseOrder receiveOrder(Long id) {
        PurchaseOrder po = getOrder(id);
        if (!"CONFIRMED".equals(po.getStatus())) {
            throw new IllegalStateException("Only CONFIRMED orders can be received. Current status: " + po.getStatus());
        }
        po.setStatus("RECEIVED");
        po.setReceivedDate(LocalDate.now());
        po.setUpdatedAt(LocalDateTime.now());
        PurchaseOrder saved = poRepository.save(po);

        List<POLineItem> lineItems = lineItemRepository.findByPurchaseOrderId(id);

        // Auto-create DRAFT cost records for each line item
        List<String> itemKeys = lineItems.stream().map(POLineItem::getItemKey).distinct().collect(Collectors.toList());
        costRecordService.createFromPO(po.getPoNumber(), po.getTotalAmount(), itemKeys, po.getCreatedBy());

        // Auto-update inventory for each line item
        for (POLineItem li : lineItems) {
            try {
                inventoryItemRepository.findByItemKey(li.getItemKey()).ifPresentOrElse(
                    item -> {
                        BigDecimal before = item.getCurrentStock() != null ? item.getCurrentStock() : BigDecimal.ZERO;
                        BigDecimal qty    = li.getQuantity() != null ? li.getQuantity() : BigDecimal.ZERO;
                        item.setCurrentStock(before.add(qty));
                        item.setLastUpdated(LocalDateTime.now());
                        inventoryItemRepository.save(item);

                        InventoryTransaction tx = new InventoryTransaction();
                        tx.setItemKey(li.getItemKey());
                        tx.setTransactionType("IN");
                        tx.setQuantity(qty);
                        tx.setBalanceAfter(before.add(qty));
                        tx.setReference(po.getPoNumber());
                        tx.setNotes("PO Receipt: " + po.getPoNumber() + " — " + po.getSupplierId());
                        tx.setPerformedBy(po.getCreatedBy() != null ? po.getCreatedBy() : "system");
                        tx.setTransactionDate(LocalDateTime.now());
                        inventoryTransactionRepository.save(tx);
                    },
                    () -> log.warn("PO receipt: inventory item not found for SKU '{}', skipping.", li.getItemKey())
                );
            } catch (Exception e) {
                log.warn("PO receipt: failed to update inventory for SKU '{}': {}", li.getItemKey(), e.getMessage());
            }
        }

        return saved;
    }

    @Auditable(entityType = "PurchaseOrder", action = "CANCEL")
    public PurchaseOrder cancelOrder(Long id) {
        PurchaseOrder po = getOrder(id);
        if (!"DRAFT".equals(po.getStatus()) && !"SUBMITTED".equals(po.getStatus())) {
            throw new IllegalStateException("Only DRAFT or SUBMITTED orders can be cancelled. Current status: " + po.getStatus());
        }
        po.setStatus("CANCELLED");
        po.setUpdatedAt(LocalDateTime.now());
        return poRepository.save(po);
    }

    public POLineItem addLineItem(Long poId, LineItemRequest req) {
        PurchaseOrder po = getOrder(poId);

        POLineItem lineItem = new POLineItem();
        lineItem.setPurchaseOrder(po);
        lineItem.setItemKey(req.itemKey());
        lineItem.setDescription(req.description());
        lineItem.setQuantity(req.quantity());
        lineItem.setUnitPrice(req.unitPrice());
        lineItem.setUom(req.uom() != null ? req.uom() : "EACH");

        POLineItem saved = lineItemRepository.save(lineItem);

        // Recalculate total amount from all line items
        List<POLineItem> allItems = lineItemRepository.findByPurchaseOrderId(poId);
        BigDecimal total = allItems.stream()
                .map(li -> li.getQuantity().multiply(li.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        po.setTotalAmount(total);
        po.setUpdatedAt(LocalDateTime.now());
        poRepository.save(po);

        return saved;
    }

    private String generatePoNumber() {
        int year = Year.now().getValue();
        long count = poRepository.count() + 1;
        return String.format("PO-%d-%04d", year, count);
    }

    public record CreatePORequest(
            String supplierId,
            LocalDate expectedDate,
            String currency,
            String notes,
            String createdBy
    ) {}

    public record LineItemRequest(
            String itemKey,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String uom
    ) {}
}

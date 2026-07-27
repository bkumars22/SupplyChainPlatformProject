/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.inventory;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "INVENTORY_ITEM")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_key", unique = true, nullable = false, length = 50)
    private String itemKey;

    @Column(name = "item_name", length = 500)
    private String itemName;

    @Column(name = "warehouse_id", length = 50)
    private String warehouseId;

    @Column(name = "current_stock", precision = 10, scale = 2)
    private BigDecimal currentStock;

    @Column(name = "reorder_point", precision = 10, scale = 2)
    private BigDecimal reorderPoint;

    @Column(name = "max_stock", precision = 10, scale = 2)
    private BigDecimal maxStock;

    @Column(name = "uom", length = 20)
    private String uom;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemKey() { return itemKey; }
    public void setItemKey(String itemKey) { this.itemKey = itemKey; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getWarehouseId() { return warehouseId; }
    public void setWarehouseId(String warehouseId) { this.warehouseId = warehouseId; }

    public BigDecimal getCurrentStock() { return currentStock; }
    public void setCurrentStock(BigDecimal currentStock) { this.currentStock = currentStock; }

    public BigDecimal getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(BigDecimal reorderPoint) { this.reorderPoint = reorderPoint; }

    public BigDecimal getMaxStock() { return maxStock; }
    public void setMaxStock(BigDecimal maxStock) { this.maxStock = maxStock; }

    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}

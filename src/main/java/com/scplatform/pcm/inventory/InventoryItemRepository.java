/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= i.reorderPoint ORDER BY i.itemKey ASC")
    List<InventoryItem> findLowStockItems();

    List<InventoryItem> findByWarehouseIdOrderByItemKeyAsc(String warehouseId);

    java.util.Optional<InventoryItem> findByItemKey(String itemKey);
}

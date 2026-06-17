/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.cost.entity.PcmItemCategoryCostRecord;
import com.scplatform.pcm.cost.util.CostRollupUtil;
import com.scplatform.pcm.item.entity.ItemCategory;

@Repository
public interface PcmItemCategoryCostRecordRepository extends JpaRepository<PcmItemCategoryCostRecord, Long> {

    @Query("""
        SELECT c FROM PcmItemCategoryCostRecord c
        WHERE c.itemCategory = :itemCategory
          AND c.contextName IS NULL
          AND c.contextType IS NULL
        """)
    Optional<PcmItemCategoryCostRecord> findItemCategoryCostRecordByNaturalKeyInternal(
        @Param("itemCategory") ItemCategory itemCategory
    );

    default PcmItemCategoryCostRecord findItemCategoryCostRecordByNaturalKey(ItemCategory itemCategory) {
        if (itemCategory == CostRollupUtil.NOCOMMODITY || CostRollupUtil.NOCOMMODITY.equals(itemCategory)) {
            return null;
        }
        return findItemCategoryCostRecordByNaturalKeyInternal(itemCategory).orElse(null);
    }

    @Query("""
        SELECT c FROM PcmItemCategoryCostRecord c
        WHERE c.itemCategory = :itemCategory
          AND c.contextName IS NULL
          AND c.contextType IS NULL
        """)
    List<PcmItemCategoryCostRecord> getFindItemCategoryCostRecordInContextCriteria(
        @Param("itemCategory") ItemCategory itemCategory
    );

    default PcmItemCategoryCostRecord findItemCategoryCostRecordInItemContext(ItemCategory itemCategory) {
        List<PcmItemCategoryCostRecord> results = getFindItemCategoryCostRecordInContextCriteria(itemCategory);
        return results.isEmpty() ? null : results.get(0);
    }
}

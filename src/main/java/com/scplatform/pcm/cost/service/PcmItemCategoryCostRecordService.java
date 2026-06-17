/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.service;

import com.scplatform.pcm.cost.util.CostRollupUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.cost.entity.PcmItemCategoryCostRecord;
import com.scplatform.pcm.cost.repository.PcmItemCategoryCostRecordRepository;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PcmItemCategoryCostRecordService {

    private final PcmItemCategoryCostRecordRepository itemCategoryCostRecordRepository;

    @Transactional(readOnly = true)
    public PcmItemCategoryCostRecord findItemCategoryCostRecordInItemContext(final ItemCategory ic,
            final Item item) {
        if (ic == null || ic == CostRollupUtil.NOCOMMODITY || ic.equals(CostRollupUtil.NOCOMMODITY)) {
            return null;
        }
        return itemCategoryCostRecordRepository.findItemCategoryCostRecordInItemContext(ic);
    }
}

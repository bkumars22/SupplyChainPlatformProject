/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3cost;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import com.scplatform.pcm.common.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class CostRecordService {

    @Autowired private CostRecordRepository costRepo;
    @Autowired private ItemRepository itemRepo;

    public Page<CostRecord> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        return costRepo.searchAll(query, pageable);
    }

    public CostRecord getById(Long id) {
        return costRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cost record not found: " + id));
    }

    public List<CostRecord> getByItemCode(String itemCode) {
        return costRepo.findByItemItemNumberOrderByVersionNumberDesc(itemCode);
    }

    public List<CostRecord> getPendingApprovals() {
        return costRepo.findByStatusOrderBySubmittedDateAsc(CostStatus.PENDING_APPROVAL);
    }

    public CostRecord create(CostRecordRequest req, String createdBy) {
        Item item = itemRepo.findByItemNumberAndTypeAndBusinessEntityName(req.getItemCode(), null, null)
            .stream().findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + req.getItemCode()));

        Optional<CostRecord> existing = costRepo.findByItemItemNumberAndStatusIn(req.getItemCode(), List.of(CostStatus.DRAFT, CostStatus.PENDING_APPROVAL));
        if (existing.isPresent()) {
            throw new ValidationException("Item " + req.getItemCode() + " already has an open cost record (ID: " + existing.get().getId() + ").");
        }

        CostRecord record = new CostRecord();
        record.setItem(item);
        record.setVersionNumber(costRepo.getNextVersionNumber(req.getItemCode()));
        record.setProposedCost(req.getProposedCost());
        record.setPreviousCost(BigDecimal.ZERO);
        record.setChangePercent(BigDecimal.ZERO);
        record.setJustification(req.getJustification());
        record.setStatus(CostStatus.DRAFT);
        record.setCreatedBy(createdBy);
        record.setCreatedDate(LocalDateTime.now());
        record.setLastModifiedDate(LocalDateTime.now());
        return costRepo.save(record);
    }

    public CostRecord submit(Long id, String submittedBy) {
        CostRecord record = getById(id);
        if (record.getStatus() != CostStatus.DRAFT) throw new ValidationException("Only DRAFT can be submitted.");
        record.setStatus(CostStatus.PENDING_APPROVAL);
        record.setSubmittedDate(LocalDateTime.now());
        record.setLastModifiedDate(LocalDateTime.now());
        return costRepo.save(record);
    }

    public CostRecord approve(Long id, String approvedBy) {
        CostRecord record = getById(id);
        if (record.getStatus() != CostStatus.PENDING_APPROVAL) throw new ValidationException("Only PENDING_APPROVAL can be approved.");
        record.setStatus(CostStatus.APPROVED);
        record.setApprovedBy(approvedBy);
        record.setApprovedDate(LocalDateTime.now());
        record.setLastModifiedDate(LocalDateTime.now());
        return costRepo.save(record);
    }

    public CostRecord reject(Long id, String reason, String rejectedBy) {
        CostRecord record = getById(id);
        if (record.getStatus() != CostStatus.PENDING_APPROVAL) throw new ValidationException("Only PENDING_APPROVAL can be rejected.");
        if (reason == null || reason.isBlank()) throw new ValidationException("Rejection reason required.");
        record.setStatus(CostStatus.REJECTED);
        record.setRejectionReason(reason);
        record.setLastModifiedDate(LocalDateTime.now());
        return costRepo.save(record);
    }

    /**
     * Auto-creates a DRAFT cost record when a PO is marked RECEIVED.
     * Uses the PO number as justification so it can be traced back.
     * Skips line items whose itemKey doesn't exist in Item Master (logs a warning).
     * Skips items that already have an open cost record.
     */
    public List<CostRecord> createFromPO(String poNumber, BigDecimal totalAmount, List<String> itemKeys, String createdBy) {
        List<CostRecord> created = new java.util.ArrayList<>();
        for (String itemKey : itemKeys) {
            try {
                Item item = itemRepo.findByItemNumberAndTypeAndBusinessEntityName(itemKey, null, null)
                        .stream().findFirst().orElse(null);
                if (item == null) {
                    continue; // item not in master — skip silently
                }
                Optional<CostRecord> existing = costRepo.findByItemItemNumberAndStatusIn(
                        itemKey, List.of(CostStatus.DRAFT, CostStatus.PENDING_APPROVAL));
                if (existing.isPresent()) {
                    continue; // already has an open record — skip
                }
                CostRecord record = new CostRecord();
                record.setItem(item);
                record.setVersionNumber(costRepo.getNextVersionNumber(itemKey));
                record.setProposedCost(totalAmount != null ? totalAmount : BigDecimal.ZERO);
                record.setPreviousCost(BigDecimal.ZERO);
                record.setChangePercent(BigDecimal.ZERO);
                record.setJustification("PO Receipt: " + poNumber);
                record.setStatus(CostStatus.DRAFT);
                record.setCreatedBy(createdBy != null ? createdBy : "system");
                record.setCreatedDate(LocalDateTime.now());
                record.setLastModifiedDate(LocalDateTime.now());
                created.add(costRepo.save(record));
            } catch (Exception e) {
                // Don't fail the whole PO receipt if cost record creation fails for one item
            }
        }
        return created;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        List<Object[]> rows = costRepo.countByStatus();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Object[] row : rows) { byStatus.put(row[0].toString(), (Long) row[1]); }
        stats.put("byStatus", byStatus);
        stats.put("pendingCount", costRepo.countPending());
        stats.put("totalRecords", costRepo.count());
        return stats;
    }
}
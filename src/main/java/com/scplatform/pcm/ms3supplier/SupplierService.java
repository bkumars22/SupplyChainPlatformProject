/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierService {

    @Autowired private SupplierRepository supplierRepo;
    @Autowired private DeliveryRepository deliveryRepo;

    public List<SupplierScorecardDto> getAllScorecards(String search) {
        List<SupplierProfile> suppliers = (search != null && !search.isBlank())
            ? supplierRepo.search(search)
            : supplierRepo.findByIsActiveTrueOrderBySupplierNameAsc();
        return suppliers.stream().map(this::toScorecard).collect(Collectors.toList());
    }

    public SupplierScorecardDto getScorecard(String supplierId) {
        SupplierProfile p = supplierRepo.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
        return toScorecard(p);
    }

    public List<SupplierDelivery> getDeliveries(String supplierId) {
        return deliveryRepo.findBySupplierSupplierIdOrderByPromisedDateDesc(supplierId);
    }

    public SupplierDelivery logDelivery(String supplierId, SupplierDeliveryRequest req) {
        SupplierProfile supplier = supplierRepo.findById(supplierId)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));

        SupplierDelivery delivery = new SupplierDelivery();
        delivery.setSupplier(supplier);
        delivery.setPoNumber(req.getPoNumber());
        delivery.setItemCode(req.getItemCode());
        delivery.setPromisedDate(req.getPromisedDate());
        delivery.setActualDate(req.getActualDate());
        delivery.setQtyOrdered(req.getQtyOrdered());
        delivery.setQtyReceived(req.getQtyReceived());
        delivery.setNotes(req.getNotes());

        // Auto-calculate delay days and status
        if (req.getActualDate() != null && req.getPromisedDate() != null) {
            long delay = ChronoUnit.DAYS.between(req.getPromisedDate(), req.getActualDate());
            delivery.setDelayDays((int) delay);
            delivery.setStatus(delay <= 0 ? DeliveryStatus.ON_TIME : DeliveryStatus.LATE);
        } else {
            delivery.setStatus(DeliveryStatus.ON_TIME);
            delivery.setDelayDays(0);
        }
        return deliveryRepo.save(delivery);
    }

    public List<SupplierScorecardDto> getAtRiskSuppliers() {
        return getAllScorecards(null).stream()
            .filter(SupplierScorecardDto::isAtRisk)
            .collect(Collectors.toList());
    }

    public List<SupplierScorecardDto> getTopSuppliers(int limit) {
        return getAllScorecards(null).stream()
            .sorted(Comparator.comparingDouble(SupplierScorecardDto::getCompositeScore).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * BB-SUP-03: tier was never actually derived from delivery history — it's
     * only ever set once, either by seed data (an arbitrary starting value)
     * or by SupplierRatingService.addRating's quality/delivery/responsiveness
     * average (a different formula entirely, unrelated to OTD). Meanwhile
     * SupplierScorecardDto computes otdScore live from real SupplierDelivery
     * rows every time a scorecard is read. Nothing ever reconciled the two,
     * so a supplier's stored tier can silently drift arbitrarily far from
     * what its actual delivery performance says. This recalculates every
     * supplier's tier from its real OTD score — call after any bulk load of
     * delivery/PO data (see DB_SETUP.md).
     *
     * Suppliers with zero delivery history are left untouched — there's no
     * OTD evidence to derive a tier from, so guessing one would just be
     * another instance of this same bug.
     */
    public Map<String, Object> recalculateTiers() {
        List<SupplierProfile> suppliers = supplierRepo.findAll();
        int changed = 0;
        int skippedNoHistory = 0;
        for (SupplierProfile p : suppliers) {
            long total = deliveryRepo.countBySupplierId(p.getSupplierId());
            if (total == 0) {
                skippedNoHistory++;
                continue;
            }
            long onTime = deliveryRepo.countOnTimeBySupplierId(p.getSupplierId());
            SupplierTier newTier = tierFromOtd(onTime * 100.0 / total);
            if (newTier != p.getTier()) {
                p.setTier(newTier);
                changed++;
            }
        }
        supplierRepo.saveAll(suppliers);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("suppliersEvaluated", suppliers.size());
        result.put("tiersChanged", changed);
        result.put("skippedNoDeliveryHistory", skippedNoHistory);
        return result;
    }

    // Same OTD bands as SupplierScorecardDto's atRisk cutoff (otdScore < 70),
    // extended into a full tier scale using the vocabulary seed data and the
    // scorecard already use (PREFERRED/APPROVED/CONDITIONAL/PROBATION).
    private SupplierTier tierFromOtd(double otdScore) {
        if (otdScore >= 95) return SupplierTier.PREFERRED;
        if (otdScore >= 85) return SupplierTier.APPROVED;
        if (otdScore >= 70) return SupplierTier.CONDITIONAL;
        return SupplierTier.PROBATION;
    }

    public Map<String, Object> getStats() {
        List<SupplierScorecardDto> all = getAllScorecards(null);
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalSuppliers", all.size());
        stats.put("atRiskCount", all.stream().filter(SupplierScorecardDto::isAtRisk).count());
        stats.put("avgCompositeScore", all.stream().mapToDouble(SupplierScorecardDto::getCompositeScore).average().orElse(0));
        stats.put("avgOtdScore", all.stream().mapToDouble(SupplierScorecardDto::getOtdScore).average().orElse(0));
        Map<String, Long> byTier = new LinkedHashMap<>();
        for (SupplierScorecardDto s : all) {
            byTier.merge(s.getTier(), 1L, Long::sum);
        }
        stats.put("byTier", byTier);
        return stats;
    }

    private SupplierScorecardDto toScorecard(SupplierProfile p) {
        Long total = deliveryRepo.countBySupplierId(p.getSupplierId());
        Long onTime = deliveryRepo.countOnTimeBySupplierId(p.getSupplierId());
        return new SupplierScorecardDto(p, total, onTime);
    }
}

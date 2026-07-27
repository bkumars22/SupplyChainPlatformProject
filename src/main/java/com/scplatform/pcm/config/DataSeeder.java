/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config;

import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.ms3cost.CostRecord;
import com.scplatform.pcm.ms3cost.CostRecordRepository;
import com.scplatform.pcm.ms3cost.CostStatus;
import com.scplatform.pcm.ms3supplier.DeliveryRepository;
import com.scplatform.pcm.ms3supplier.DeliveryStatus;
import com.scplatform.pcm.ms3supplier.SupplierDelivery;
import com.scplatform.pcm.ms3supplier.SupplierProfile;
import com.scplatform.pcm.ms3supplier.SupplierRepository;
import com.scplatform.pcm.ms3supplier.SupplierService;
import com.scplatform.pcm.ms3supplier.SupplierTier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final AlertDetailRepository alertRepo;
    private final ItemRepository itemRepo;
    private final CostRecordRepository costRepo;
    private final SupplierRepository supplierRepo;
    private final DeliveryRepository deliveryRepo;
    private final SupplierService supplierService;

    public DataSeeder(AlertDetailRepository alertRepo, ItemRepository itemRepo,
                      CostRecordRepository costRepo, SupplierRepository supplierRepo,
                      DeliveryRepository deliveryRepo, SupplierService supplierService) {
        this.alertRepo = alertRepo;
        this.itemRepo = itemRepo;
        this.costRepo = costRepo;
        this.supplierRepo = supplierRepo;
        this.deliveryRepo = deliveryRepo;
        this.supplierService = supplierService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            seedAlerts();
            seedCostRecords();
            boolean seededNewSuppliers = seedSuppliers();
            if (seededNewSuppliers) {
                seedSupplierDeliveries();
                // BB-SUP-03: without this, freshly-seeded suppliers keep
                // their arbitrary starting tier forever, disconnected from
                // the delivery history just seeded above for them — the
                // exact mismatch this bug is about. Real bulk loads should
                // call POST /api/suppliers/recalculate-tiers themselves
                // (see DB_SETUP.md); seeding does it inline since there's
                // no separate "load" step to hook it onto here.
                Map<String, Object> result = supplierService.recalculateTiers();
                log.info("Recalculated supplier tiers from seeded delivery history: {}", result);
            }
            log.info("DataSeeder done - alerts:{} costs:{} suppliers:{}",
                alertRepo.count(), costRepo.count(), supplierRepo.count());
        } catch (Exception e) {
            log.warn("DataSeeder skipped: {}", e.getMessage());
        }
    }

    private void seedAlerts() {
        if (alertRepo.count() > 0) return;
        List.of(
            a("COST_SPIKE","ALERT-001","COST_CHANGE","PCB-001 cost increased 25%","PCB-001 rose from $48 to $60."),
            a("SUPPLIER_RISK","ALERT-002","SUPPLIER_PERFORMANCE","SUPP-003 OTD below 70%","Taiwan Semi OTD dropped to 62%."),
            a("BOM_CHANGE","ALERT-003","BOM_UPDATE","BOM-LAPTOP-001 updated","PCB-002 quantity changed from 1 to 2."),
            a("LEAD_TIME","ALERT-004","SUPPLY_RISK","CHIP-001 lead time 16 weeks","Foxconn extended lead time."),
            a("PENDING","ALERT-005","WORKFLOW","3 cost records pending","PCB-001, CAP-100UF pending."),
            a("INVENTORY","ALERT-006","INVENTORY","CAP-100UF below safety stock","150 units vs 500 safety stock."),
            a("CONTRACT","ALERT-007","CONTRACT","Murata contract expiring in 30d","RES-10K pricing expires 2025-01-31."),
            a("QUALITY","ALERT-008","QUALITY","PCB-003 defect rate 3.2%","Above 2.0% threshold.")
        ).forEach(alertRepo::save);
        log.info("Seeded {} alerts", alertRepo.count());
    }

    private AlertDetail a(String label, String id, String type, String summary, String detail) {
        AlertDetail x = new AlertDetail();
        x.setAlertLabel(label); x.setAlertId(id); x.setAlertType(type);
        x.setShortSummary(summary); x.setLongSummary(detail);
        x.setUserLoginId("kumar"); x.setState(AlertDetailState.ACTIVE);
        x.setCreated(LocalDate.now());
        return x;
    }

    private void seedCostRecords() {
        if (costRepo.count() > 0) return;
        itemRepo.findAll().forEach(item -> {
            String code = item.getItemNumber();
            if (code == null) return;
            CostRecord r = new CostRecord();
            r.setItem(item); r.setVersionNumber(1); r.setCreatedBy("kumar");
            switch (code) {
                case "PCB-001"  -> { r.setProposedCost(BigDecimal.valueOf(60.0));  r.setPreviousCost(BigDecimal.valueOf(48.0));  r.setChangePercent(BigDecimal.valueOf(25.00)); r.setStatus(CostStatus.PENDING_APPROVAL); r.setJustification("Shenzhen increased cost 25%"); }
                case "PCB-002"  -> { r.setProposedCost(BigDecimal.valueOf(35.5));  r.setPreviousCost(BigDecimal.valueOf(32.0));  r.setChangePercent(BigDecimal.valueOf(10.94)); r.setStatus(CostStatus.DRAFT);            r.setJustification("Annual review PCB-002"); }
                case "CHIP-001" -> { r.setProposedCost(BigDecimal.valueOf(125.0)); r.setPreviousCost(BigDecimal.valueOf(118.0)); r.setChangePercent(BigDecimal.valueOf(5.93));  r.setStatus(CostStatus.APPROVED);         r.setJustification("Chip shortage premium"); r.setApprovedBy("ADMIN"); }
                case "CHIP-002" -> { r.setProposedCost(BigDecimal.valueOf(89.0));  r.setPreviousCost(BigDecimal.valueOf(72.0));  r.setChangePercent(BigDecimal.valueOf(23.61)); r.setStatus(CostStatus.REJECTED);         r.setJustification("Spot market CHIP-002"); r.setRejectionReason("Exceeds 20% threshold"); }
                case "CAP-100UF"-> { r.setProposedCost(BigDecimal.valueOf(0.85));  r.setPreviousCost(BigDecimal.valueOf(0.75));  r.setChangePercent(BigDecimal.valueOf(13.33)); r.setStatus(CostStatus.PENDING_APPROVAL); r.setJustification("Delta supply constraint"); }
                case "RES-10K"  -> { r.setProposedCost(BigDecimal.valueOf(0.025)); r.setPreviousCost(BigDecimal.valueOf(0.020)); r.setChangePercent(BigDecimal.valueOf(25.00)); r.setStatus(CostStatus.DRAFT);            r.setJustification("RES-10K annual review"); }
                default -> { return; }
            }
            costRepo.save(r);
        });
        log.info("Seeded {} cost records", costRepo.count());
    }

    /** @return true if suppliers were actually seeded just now (false if they already existed). */
    private boolean seedSuppliers() {
        if (supplierRepo.count() > 0) return false;
        List.of(
            s("SUPP-001","Shenzhen Electronics Co.","China",  SupplierTier.PREFERRED,   88.0, 95.0),
            s("SUPP-002","Foxconn Technology Group", "Taiwan", SupplierTier.APPROVED,    90.0, 78.0),
            s("SUPP-003","Taiwan Semiconductors Ltd","Taiwan", SupplierTier.CONDITIONAL, 75.0, 70.0),
            s("SUPP-004","Murata Manufacturing",     "Japan",  SupplierTier.PREFERRED,   99.0, 92.0),
            s("SUPP-005","Delta Electronics India",  "India",  SupplierTier.PROBATION,   65.0, 60.0)
        ).forEach(supplierRepo::save);
        log.info("Seeded {} suppliers", supplierRepo.count());
        return true;
    }

    /**
     * BB-SUP-03: seed real delivery history per supplier, roughly matching
     * the tier each was just given above — otherwise otdScore (computed live
     * from this table by SupplierScorecardDto) reads 0%/atRisk=true for
     * every supplier immediately after a fresh seed, regardless of tier.
     * 20 POs per supplier, on-time ratio tuned to land in that supplier's
     * intended OTD band (see SupplierService.tierFromOtd).
     */
    private void seedSupplierDeliveries() {
        seedDeliveriesForSupplier("SUPP-001", 20, 19); // 95% -> PREFERRED band
        seedDeliveriesForSupplier("SUPP-002", 20, 17); // 85% -> APPROVED band
        seedDeliveriesForSupplier("SUPP-003", 20, 15); // 75% -> CONDITIONAL band
        seedDeliveriesForSupplier("SUPP-004", 20, 20); // 100% -> PREFERRED band
        seedDeliveriesForSupplier("SUPP-005", 20, 12); // 60% -> PROBATION band
        log.info("Seeded {} supplier delivery records", deliveryRepo.count());
    }

    private void seedDeliveriesForSupplier(String supplierId, int totalDeliveries, int onTimeCount) {
        SupplierProfile supplier = supplierRepo.findById(supplierId).orElse(null);
        if (supplier == null) return;

        LocalDate promised = LocalDate.now().minusDays(totalDeliveries * 7L);
        for (int i = 0; i < totalDeliveries; i++) {
            boolean onTime = i < onTimeCount;
            LocalDate promisedDate = promised.plusDays(i * 7L);
            LocalDate actualDate = onTime ? promisedDate : promisedDate.plusDays(4 + (i % 5));

            SupplierDelivery d = new SupplierDelivery();
            d.setSupplier(supplier);
            d.setPoNumber("PO-" + supplierId + "-" + (1000 + i));
            d.setItemCode("SEED-ITEM-" + (i % 3));
            d.setPromisedDate(promisedDate);
            d.setActualDate(actualDate);
            d.setQtyOrdered(100);
            d.setQtyReceived(100);
            d.setStatus(onTime ? DeliveryStatus.ON_TIME : DeliveryStatus.LATE);
            d.setDelayDays((int) java.time.temporal.ChronoUnit.DAYS.between(promisedDate, actualDate));
            deliveryRepo.save(d);
        }
    }

    private SupplierProfile s(String id, String name, String country,
                               SupplierTier tier, double quality, double resp) {
        SupplierProfile p = new SupplierProfile();
        p.setSupplierId(id); p.setSupplierName(name); p.setCountry(country);
        p.setTier(tier); p.setQualityScore(quality); p.setResponsivenessScore(resp);
        p.setIsActive(true);
        return p;
    }
}
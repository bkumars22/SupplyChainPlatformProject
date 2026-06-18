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
import com.scplatform.pcm.ms3supplier.SupplierProfile;
import com.scplatform.pcm.ms3supplier.SupplierRepository;
import com.scplatform.pcm.ms3supplier.SupplierTier;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.role.repository.RoleRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Resets the demo database to clean seed data.
 * Called by DatabaseResetScheduler (nightly) and POST /api/admin/reset-demo (manual).
 *
 * Deletion order respects FK constraints:
 *   alerts → cost_records → suppliers (cascade deliveries) → upsert users
 */
@Component
public class DatabaseSeeder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final AlertDetailRepository alertRepo;
    private final CostRecordRepository  costRepo;
    private final SupplierRepository    supplierRepo;
    private final ItemRepository        itemRepo;
    private final UsersRepository       usersRepo;
    private final RoleRepository        roleRepo;
    private final BCryptPasswordEncoder bcrypt;

    public DatabaseSeeder(AlertDetailRepository alertRepo,
                          CostRecordRepository costRepo,
                          SupplierRepository supplierRepo,
                          ItemRepository itemRepo,
                          UsersRepository usersRepo,
                          RoleRepository roleRepo,
                          BCryptPasswordEncoder bcrypt) {
        this.alertRepo    = alertRepo;
        this.costRepo     = costRepo;
        this.supplierRepo = supplierRepo;
        this.itemRepo     = itemRepo;
        this.usersRepo    = usersRepo;
        this.roleRepo     = roleRepo;
        this.bcrypt       = bcrypt;
    }

    @Transactional
    public void resetToSeedData() {
        long start = System.currentTimeMillis();

        // 1. Alerts — no children, safe batch delete
        alertRepo.deleteAllInBatch();

        // 2. Cost records — FK to Item (items not deleted), no children
        costRepo.deleteAllInBatch();

        // 3. Suppliers — cascade CascadeType.ALL handles SupplierDelivery children
        supplierRepo.deleteAll();

        // 4. Re-insert seed data
        seedAlerts();
        seedSuppliers();
        seedCostRecords();
        upsertUsers();

        log.info("Database reset completed at {} in {}ms",
                LocalDateTime.now(), System.currentTimeMillis() - start);
    }

    // ── Alerts ────────────────────────────────────────────────────────────────

    private void seedAlerts() {
        List.of(
            alert("ALT-DEMO-001", "HIGH_RISK",
                  "TechParts India OTD dropped below 45%",
                  "OTD score of 42% — below the 45% minimum threshold. Systemic delivery failure pattern detected.",
                  "HIGH"),
            alert("ALT-DEMO-002", "CRITICAL_RISK",
                  "PrecisionMfg defect rate exceeded 10%",
                  "Defect rate 11.7% is 3× above the 4% acceptable threshold. Batch rejection events spiking.",
                  "CRITICAL"),
            alert("ALT-DEMO-003", "WARNING",
                  "Lead time variance increased 40%",
                  "Supply chain disruption detected. EU logistics corridor lead time variance suggests port congestion.",
                  "MEDIUM")
        ).forEach(alertRepo::save);
        log.info("Seeded {} alerts", alertRepo.count());
    }

    private AlertDetail alert(String alertId, String type, String summary, String detail, String severity) {
        AlertDetail a = new AlertDetail();
        a.setAlertId(alertId);
        a.setAlertLabel(summary);
        a.setAlertType(type);
        a.setShortSummary(summary);
        a.setLongSummary(detail);
        a.setStringAttribute1(severity);   // severity stored in STRING_ATTRIBUTE1
        a.setUserLoginId("kumar");
        a.setState(AlertDetailState.ACTIVE);
        a.setCreated(LocalDate.now());
        return a;
    }

    // ── Suppliers ─────────────────────────────────────────────────────────────

    private void seedSuppliers() {
        // Task tiers (GOLD/SILVER/AT_RISK/CRITICAL) mapped to existing SupplierTier enum:
        //   GOLD     → PREFERRED  |  SILVER → APPROVED
        //   AT_RISK  → PROBATION  |  CRITICAL → PROBATION
        List.of(
            supplier("SUPP-001", "TechParts India Pvt Ltd",   "India",     SupplierTier.PROBATION,  8.3,  5.0),
            supplier("SUPP-002", "GlobalComp Singapore",       "Singapore", SupplierTier.PREFERRED,  1.2,  9.5),
            supplier("SUPP-003", "SwiftLogix Dubai",           "UAE",       SupplierTier.APPROVED,   4.1,  7.0),
            supplier("SUPP-004", "PrecisionMfg Chennai",       "India",     SupplierTier.PROBATION, 11.7,  3.5),
            supplier("SUPP-005", "NexusParts Germany",         "Germany",   SupplierTier.PREFERRED,  0.8,  9.8)
        ).forEach(supplierRepo::save);
        log.info("Seeded {} suppliers", supplierRepo.count());
    }

    private SupplierProfile supplier(String id, String name, String country,
                                     SupplierTier tier, double defectRate, double responsiveness) {
        SupplierProfile p = new SupplierProfile();
        p.setSupplierId(id);
        p.setSupplierName(name);
        p.setCountry(country);
        p.setTier(tier);
        p.setQualityScore(defectRate);          // defect rate % — lower is better
        p.setResponsivenessScore(responsiveness); // 1-10 rating
        p.setIsActive(true);
        p.setOnboardedDate(LocalDate.of(2024, 1, 1));
        return p;
    }

    // ── Cost Records ──────────────────────────────────────────────────────────

    private void seedCostRecords() {
        // Use whatever items exist in the database; seed up to 4 demo cost records.
        // If specific item codes exist they are preferred; otherwise use the first available items.
        var items = itemRepo.findAll();
        if (items.isEmpty()) {
            log.warn("No items found — skipping cost record seeding");
            return;
        }

        record DemoRecord(String justification, BigDecimal cost, CostStatus status) {}
        var demoData = List.of(
            new DemoRecord("Raw Materials Q2 — bulk discount negotiated with 3 vendors",
                           new BigDecimal("340000"), CostStatus.APPROVED),
            new DemoRecord("Logistics Optimization — new routing algorithm reduces cost 25%",
                           new BigDecimal("89500"),  CostStatus.DRAFT),
            new DemoRecord("Supplier Audit Program — expanded to cover high-risk tier suppliers",
                           new BigDecimal("45200"),  CostStatus.APPROVED),
            new DemoRecord("Emergency Procurement — triggered by TechParts India supply disruption",
                           new BigDecimal("127800"), CostStatus.PENDING_APPROVAL)
        );

        int limit = Math.min(demoData.size(), items.size());
        for (int i = 0; i < limit; i++) {
            var d = demoData.get(i);
            var item = items.get(i);
            CostRecord r = new CostRecord();
            r.setItem(item);
            r.setVersionNumber(1);
            r.setCreatedBy("kumar");
            r.setJustification(d.justification());
            r.setProposedCost(d.cost());
            r.setStatus(d.status());
            r.setCreatedDate(LocalDateTime.now());
            if (d.status() == CostStatus.APPROVED) r.setApprovedBy("admin");
            costRepo.save(r);
        }
        log.info("Seeded {} cost records", costRepo.count());
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    private void upsertUsers() {
        Role managerRole = findOrCreateRole("MANAGER", "Supply Chain Manager");
        Role adminRole   = findOrCreateRole("ADMIN",   "Administrator");
        Role viewerRole  = findOrCreateRole("VIEWER",  "Viewer");

        upsertUser("kumar",  "Kumar Swamy",   "kumar@scip.com",  bcrypt.encode("Kumar@2026"),  managerRole);
        upsertUser("admin",  "Admin User",    "admin@scip.com",  bcrypt.encode("Admin@2026"),  adminRole);
        upsertUser("viewer", "Viewer User",   "viewer@scip.com", bcrypt.encode("Viewer@2026"), viewerRole);

        log.info("Demo users upserted: kumar / admin / viewer");
    }

    private Role findOrCreateRole(String roleId, String roleName) {
        Role role = roleRepo.findRoleById(roleId);
        if (role == null) {
            role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName);
            role.setPermRole(false);
            role = roleRepo.save(role);
            log.info("Created role: {}", roleId);
        }
        return role;
    }

    private void upsertUser(String userId, String userName, String email, String encodedPwd, Role role) {
        List<Users> existing = usersRepo.findAllByUserId(userId);
        Users u = existing.isEmpty() ? new Users() : existing.get(0);
        u.setUserId(userId);
        u.setUserName(userName);
        u.setEmailAddress(email);
        u.setPassword(encodedPwd);
        u.setRole(role);
        u.setIsEnabled(true);
        usersRepo.save(u);
    }
}
